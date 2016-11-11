# Hierarchical Classifier for Energy Disaggregation
----
### This repository represents independent work done as _Research Fellow_ at **Williams College**.     
### Project Experience Included : 
  * Designing an original machine learning algorithm to learn individual circuit use from aggregate data with _80%_ accuracy
  * Collecting, cleaning, and formatting large sets of electrical data in *.csv* format
  * Hands on with a 'Full Stack' machine learning solution - _data collection, cloud storage, data cleaning, algorithm design, training, and testing_ 

## Motivation: 

A sustainable home depends upon intelligent decisions at the individual appliance level; however, gathering such data is an expensive and involved process, requiring the installation of a monitoring device at every load point. Thus, the ability to extrapolate individual loads from aggregate data would be an exciting contribution to the pursuit of a sustainable home. 

In other words, a home's energy usage over time is a function of its appliances, systems, and devices. In order to make a home energy efficient, one must optimize this function. Such an optimization takes many interesting forms - scheduling of HVAC systems, load curtailment based upon occupancy detection, etc. - but *each* requires knowledge of the level, and combination, of individual energy loads over time.  

## Approach:

The system represents a machine learning approach to solving the problem. In particular, we became interested in investigating the theory behind decision trees and the potential of leveraging the data at each node to train an individual classifier.

We use two different datasets of individual circuit data - a single home in a proof concept phase, and a neighborhood of homes in an attempt to generalize the system. In the first phase we rely on the WEKA machine learning library to execute our experiments, but in generalizing to other homes we also implement a java program to dynamically generate our classifier tree. 

We train two layers of J48 decision tree algorithms: the root classifier being trained on all examples, and the second level being classifiers trained on instances correctly classified as class A by the root classifier, and instances of class B incorrectly classified as class A by the root classifier. In this way we create a final classifier which is a "tree of decision trees", classifying instances by first testing at the root classifier and then at the corresponding second level classifier. The second level classifiers represent an error checking mechanism - reducing the number of false positive classifications.   

## Data:
The system leverages labeled data ( at 5 second intervals) from 21 circuits in Professor Jeannie Albrecht's Massachusetts home. The data is gathered from 6 weeks published to eGauge cloud monitoring system. We format 6 weeks of energy usage data from 21 circuits in a single residential home by transforming raw wattage (kW) to a binary - on/off - label. Then, we experiment with a variety of clustering algorithms to reduce the number of classes from 21^2 to around 12 of the most common and accurate classes. The amount of formatting needed by data in this raw state was a great learning experience - a great portion of the work in designing a general, robust machine learning system seems to be in ensuring the data represent the problem one is trying to solve. 

## Project Timeline:

### Phase 1 : Proof-of-Concept "home"

Train on a single home, test on the same home. 

### Phase 2 : Generalization "neighborhood"

Train on a neighborhood of homes, test on a separate (but similar in profile) home.

## Code: 

### Data Formatting 
This was my first attempt at designing a machine learning system, and the biggest learning curve was certainly in formatting the data. The result of trimming, formatting field values, and splitting into training and test sets is a series of (often hardcoded and ugly) awk scripts. Lately, I have been pursuing more general, python based, data formatting schemes.

'reducer.awk' : used to merge cluster titles 

'timeFormatter.awk' : used to map time=0 to something other than 1200am

'split.awk' : split a full dataset into its corresponding weeks based upon number of instances per week

'onoff.awk' : change raw values into on/off (1/0) values 

'merger.py' : used to merge the three circuit's data into one instance

'format.awk' : change timestamp into day(weekend/weekday) and time(0-235900) value

'conv2num.awk' : change nominal attributes into numeric by adding 0

'clusterwise.awk' : split a dataset by cluster

'buildTrain.awk' : build a training set with equal numbers of each cluster

'sort.awk' : change the order of the attributes

### Classifier 
Generalizing to multiple homes also brought abstracting the classifier tree process into a Java implementation. The WEKA open source library provides J48 decision tree algorithms which we dynamically generate, train via the process described in our **Approach** section. 

The classifier generation algorithm is implemented by *HierarchicalDisaggregator.java*. The input is a training dataset and a parameter for tree depth - the output is a tree of classifiers of the specified depth. The algorithm is recursive to allow for experimentation with multi-level classifier trees. In fact, such generation need not be linear and static, but perhaps be logistic, or dynamic in the same way that our own brain grows and exhibits neuro-plasticity 
