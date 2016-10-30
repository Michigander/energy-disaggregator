BEGIN{
FS=","
OFS=","
}
{
cap1 = $5 
cap2 = $4
cap3 = $6

patch1 = $16  
patch2 = $17
patch3 = $18

$4 = $19

# Grab & Store 20-30 To replace 5-15
for(i=20 ; i<31;i++) a[i] = $i;
# Replace 16-27 
for(i=16;i<28;i++) $i = $(i-9);
# Replace 5 - 15
for(i=5 ; i<16;i++) $i = a[i+15];
$25 = patch1
$26 = patch2
$27 = patch3
$28 = cap1
$29 = cap2
$30 = cap3
print $0
}