import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import java.io.{BufferedWriter, File, FileWriter}
import scala.concurrent.Future
import scala.math._
import scala.util.{Failure, Random, Success}




object Main {

  implicit val system: ActorSystem = ActorSystem("StreamML")
  implicit val materializer: ActorMaterializer =ActorMaterializer()
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val input: Source[(Double, Double), NotUsed] = Source(Random.shuffle( (1 to 1000 ).map(x => (x.toDouble,sqrt(x.toDouble)))))

  // Calculate the mean of y and x. Define index counter (only need to do this once).
  val mean: Flow[(Double, Double), (Double, Double, Double, Double, Double), NotUsed] = Flow[(Double,Double)].statefulMapConcat {
    () =>

      var x_avg:Double = 0
      var y_avg:Double = 0
      var index: Double = 0

      // Return the function that will be invoked for each element.
      { in =>
        x_avg = (x_avg*index + in._1) / (index+1)
        y_avg = (y_avg*index + in._2) / (index+1)
        val zipped = (index,in._1, in._2, x_avg,y_avg)
                    //(index,x,y,x_avg,y_avg)
        index += 1
        // Return an iterable with the single element.
        zipped :: Nil
      }
  }

  //Calculate the average of the second derivative.
  val dx: Flow[(Double, Double, Double, Double, Double), (Double, Double, Double, Double, Double, Double, Double), NotUsed] = Flow[(Double,Double,Double,Double,Double)].statefulMapConcat {
    () =>
      var dx_avg:Double = 0
      var last: (Double,Double) = (0,0)
      var d: Double = 0

      // Return the function that will be invoked for each element.
      { in =>
        d = if(in._2>last._1){(in._3-last._2)/(in._2-last._1)} else{(last._2-in._3)/(last._1-in._2)}
        dx_avg = (dx_avg * in._1 + d)/(in._1+1)
        val zipped = (in._1,in._2,in._3,in._4,in._5,d,dx_avg)
        //(index,x,y,x_avg,y_avg,d_local,dx_avg)
        last = (in._2,in._3)
        // Return an iterable with the single element
        zipped :: Nil
      }
  }

  val dx2: Flow[(Double, Double, Double, Double, Double, Double, Double), (Double, Double, Double, Double, Double, Double, Double, Double, Double), NotUsed] = Flow[(Double,Double,Double,Double,Double,Double,Double)].statefulMapConcat {
    () =>
      var dx2_avg:Double = 0
      var last: (Double,Double) = (0,0)
      var d2: Double = 0

      // Return the function that will be invoked for each element.
      { in =>
        d2 = if(in._2>last._1){(in._6-last._2)/(in._2-last._1)} else{(last._2-in._6)/(last._1-in._2)}
        dx2_avg = (dx2_avg * in._1 + d2)/(in._1+1)
        val zipped = (in._1,in._2,in._3,in._4,in._5,in._6,in._7,d2,dx2_avg)
        //(index,x,y,x_avg,y_avg,d_local,dx_avg,d2_local,dx2_avg)
        last = (in._2,in._6)
        // Return an iterable with the single element
        zipped :: Nil
      }
  }

  val dx3: Flow[(Double, Double, Double, Double, Double, Double, Double, Double, Double), (Double, Double, Double, Double, Double, Double, Double, Double, Double, Double), NotUsed] = Flow[(Double,Double,Double,Double,Double,Double,Double,Double,Double)].statefulMapConcat {
    () =>
      var dx3_avg:Double = 0
      var last: (Double,Double) = (0,0)
      var d3: Double = 0

      // Return the function that will be invoked for each element.
      { in =>
        d3 = if(in._2>last._1){(in._8-last._2)/(in._2-last._1)} else{(last._2-in._8)/(last._1-in._2)}
        dx3_avg = (dx3_avg * in._1 + d3)/(in._1+1)
        val zipped = (in._1,in._2,in._3,in._4,in._5,in._6,in._7,in._8,in._9,dx3_avg)
        //(index,x,y,x_avg,y_avg,d_local,dx_avg,d2_local,dx2_avg,dx3_avg)
        last = (in._2,in._8)
        // Return an iterable with the single element
        zipped :: Nil
      }
  }


  val predict: Flow[(Double, Double, Double, Double, Double, Double, Double, Double, Double, Double), (Double, Double), NotUsed] = Flow[(Double,Double,Double,Double,Double,Double,Double,Double,Double,Double)].map(in => (in._3,  in._5 + (in._7 * (in._2 - in._4))  + (in._9 * pow(in._2 - in._4,2))/2  + (in._10 * pow(in._2 - in._4,3))/6))

  //relative measure of error / actual
  val measure: Flow[(Double, Double), Double, NotUsed] = Flow[(Double,Double)].map(in => abs( (in._1 - in._2)/in._1)*100)

  val sink: Sink[Double, Future[Seq[Double]]] = Sink.seq[Double]

  val result: Future[Seq[Double]] = input.via(mean).via(dx).via(dx2).via(dx3).via(predict).via(measure).runWith(sink)



  val data: Unit = result.onComplete{
    case Success(data) => {
      println(data.length)
      println(data.sum/data.length)
      val file = new File("test.csv")
      val bw = new BufferedWriter(new FileWriter(file))
      for (line <- data.map(x => x.toString)) {
        bw.write(line+"\n")
      }
      bw.close()
      //result.foreach(println)
      system.terminate()
    }



    case Failure(exception) => println(s"yikes $exception")
      system.terminate()
  }





  def main(args: Array[String]): Unit = {
    println("")
  }
}