BEGIN{
FS = "," 
OFS = ","
}
NR<2{
print $0
}
NR>1{
    if($5 == "cluster11" || $6 == "cluster11"){
	if( $5 != "cluster6" && $6 != "cluster6" ){
	    print $0;
	}
    }
}
END{

}