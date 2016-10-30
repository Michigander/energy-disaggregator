BEGIN{
FS = ","
OFS = ","
}

# print header
NR<2{
print $0
}

# print only instances which are cluster 6, or predicted cluster 6
NR>1{
    if( $5 == "cluster6" || $6 == "cluster6" ){
	if($5 == $6){
	    right++;
	}else wrong++;
    }
}

END{
print right
print wrong
print right/wrong 
}