def update = {
  "\b" * 105 + "." * it + " " * (100 - it) + "[${it}]"
}

(1..100).each {
  print update(it)
  sleep 500 
}
