# group certain clusters into one 
BEGIN{
FS = ","
OFS = ","
}
NR<2{
print $0
}
NR>1{
    if( $24 == "cluster8" || $24 == "cluster13" || $24 == "cluster0"){ $24 = "cluster11" }
      print $0 
}