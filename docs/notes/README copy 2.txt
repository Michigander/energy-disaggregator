Programs:

THIS IS A FILE OF PROGRAMS IN VARIOUS FORMS.
MANY AWK PROGRAMS WILL HAVE DATASET-SPECIFIC VARIABLES.
HOPEFULLY THEY ARE TITLED DISTINCTLY ENOUGH TO BE CHANGED AS NECESSARY.

reducer.awk : used to merge cluster titles 

timeFormatter.awk : used to map time=0 to something other than 1200am

split.awk : split a full dataset into its corresponding weeks based upon number of instances per week

onoff.awk : change raw values into on/off (1/0) values 

merger.py : used to merge the three circuit's data into one instance

format.awk : change timestamp into day(weekend/weekday) and time(0-235900) value

conv2num.awk : change nominal attributes into numeric by adding 0

clusterwise.awk : split a dataset by cluster

buildTrain.awk : build a training set with equal numbers of each cluster

-sort.awk : change the order of the attributes

NOTE : 

other versions of these programs may exist within dataset folders if i do not get the time 
to clean them out. 
