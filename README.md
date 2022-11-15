<h1> Approximating Stream-Data with Taylor Series and Akka Streams </h1>

Functions that are infinietly differentiable about a point, $a$, can be approximated using a Taylor Series

```math
f(x) \approx \sum_{n=0}^{\infty} \frac{f^n(a)(x-a)^n}{n!} . 
```

This code uses Akka streams to attempt build a third order approximation of an unknown function $f(x)$ given an input of pairs $(x,f(x))$, where the third order approximation is, instead of building around a different point $a$ each time, uses the averages of the inputs and an average of the first,second and third derivative through an averaging of the average rate of changes between sequential inputs. \n

The approximation does well so far when the unknown function is linear,

![alt text](Linear.png)

but not so well in the non-linear case,

![alt text](Root.png)
![alt text](Log.png)

Two problems with this method is that every average rate of change if worth the same in the approximation of the derivatives and sequential data points may be far apart, where ideally we would calculate the average rate of change over small intervals. It might be possible to weight the effect of each average rate of change such that small-distance rates of change are valued more than larger ones.





