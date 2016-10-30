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
    cluster = $24
    if( cluster == "cluster0" || cluster == "cluster10" ) $24 = "cluster11";
    if( cluster == "cluster6" ) $24 = "cluster1";
    if( cluster == "cluster13" )$24 = "cluster8";
    print $0
}
