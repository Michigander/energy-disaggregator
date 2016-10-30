# reduce a dataset to the grouping for set D
# {((0,10),11)(6,1)(13,8)} // mapping for reducing clusters
BEGIN{
FS = "," 
OFS = ","
}
NR<2{
print $0
}
NR>1{
    cluster = $6
    if( cluster == "cluster1" || cluster == "cluster6" ) $6 = "cluster8";
    if( cluster == "cluster0" ) $6 = "cluster11";

    print $0
}
