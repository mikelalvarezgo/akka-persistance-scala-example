import scala.collection.JavaConverters._
// you can write to stdout for debugging purposes, e.g.
// println("this is a debug message")
def isArithmetic(diff: Int, rest:List[Int]): Boolean ={
  if(rest.length == 1){
    return true
  }
  else {
    if(rest.head - rest.tail.head == diff){
      isArithmetic(diff, rest.tail)
    }
    else{
      return false
    }
  }
}
val prueba1 = List(1,3,5,7,9)
val prueba2 = List(1,3,4,7,9)
val test1 =isArithmetic(prueba1.head - prueba1.tail.head,prueba1.tail)
println(test1)
//def isSliceAritmethic(slice: List[Int]):Boolean = {
//}
