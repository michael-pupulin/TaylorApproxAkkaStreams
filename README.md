<h1> Approximating Stream-Data with Taylor Series and Akka Streams </h1>

Functions that are infinietly differentiable about a point, $a$, can be approximated using a Taylor Series

```math
f(x) \approx \sum_{n=0}^{\infty} \frac{f^n(a)(x-a)^n}{n!} . 
```

This code uses Akka streams to attempt build a third order approximation of an unknown function $f(x)$ given an input of pairs $(x,f(x))$, where the third order approximation is, instead of building around a different point $a$ each time, uses the averages of the inputs and an average of the first,second and third derivative through an averaging of the average rate of changes between sequential inputs. \n

The approximation does well so far when the unknown function is linear,

![alt text](https://github.com/[username]/[reponame]/blob/[branch]/image.jpg?raw=true)

but not so well in the non-linear case,

![alt text](https://github.com/[username]/[reponame]/blob/[branch]/image.jpg?raw=true)






