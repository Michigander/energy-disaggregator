# reduce a dataset to merged clusters

BEGIN{
FS = "," 
OFS = ","
}
NR<2{
print $0
}
NR>1{
    cluster = $5
    if( cluster == "cluster14" ) $5 = "cluster18";
    if( cluster == "cluster1" || cluster == "cluster10"|| cluster =="cluster2" || cluster == "cluster8" ) $5 = "cluster6";
    if( cluster == "cluster0" || cluster == "cluster13" ) $5 = "cluster11";
    print $0
}
