# reduce a dataset to the grouping for set B
# {(10,0)(9,6)(1,8)(13,11)} // mapping for reducing clusters
BEGIN{
FS = "," 
OFS = ","
}
NR<2{
print $0
}
NR>1{
    cluster = $24
    if( cluster == "cluster10" ) $24 = "cluster0";
    if( cluster == "cluster9" ) $24 = "cluster6";
    if( cluster == "cluster1" )$24 = "cluster8";
    if( cluster == "cluster13" ) $24 = "cluster11";
    print $0
}
