"""
Program that make new training file from the original data files by adding random noise to the data for 
generalisation of th NN

For every data-file (this is the data collected from the simple driver for 1 track). Make 10 new files.
One of them is the original file, the other one 9 are the same files with added noise on the input data.
Target data remains the same because we want to map the input data always to the middle of the track.

Also a test set will be made, for every track, this contains 10% of the data for every track.   

"""
import os
import numpy as np 
import csv
import datetime
import sys
from random import shuffle

DATA_FOLDER = "/Users/Maurits/Documents/UvA AI/Computational Intelligene /torcs data project/data"
DATA_OUPUT_FOLDER = "/Users/Maurits/Documents/UvA AI/Computational Intelligene /torcs data project/data/data output/"

GENERATE_SEPARATED_DATA_FILES = False 
GENERATE_TEST_AND_TRAINING_FILES = True


"""

Function that adds noise on a data_vector 
params: data_array and distribution of the noise
:return: An noisy version of the origiginal input data.
:rtype: array
"""

def created_noisy_data(data_array, noise_distribution):
    std = 1 # standard deviation of the noise 
    if noise_distribution == "normal":
    	return np.random.normal(data_array,std)
    if noise_distribution == "origiginal":
        return np.array(data_array)
    
    # TODO: do we need more distributions ?
    

def generate_separated_data_files(DATA_FOLDER, DATA_OUPUT_FOLDER):
    output_folder = DATA_OUPUT_FOLDER + "Separeted_training_datasets_" + datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    #os.makedirs(output_folder)
    for root, dirs, files in os.walk(DATA_FOLDER):
        for file in files:
            if file.endswith(".dat"):
                filename = root + "/" + file
                input_data_file = open(filename, 'r')
                # skip first two lines because they are headers 
                raw_data_file = input_data_file.readlines()
                data_headers = raw_data_file[:2]
                data_file = raw_data_file[2:] #array with strings for every data_point 
                track_name =  os.path.splitext(file)[0]
                print "makes noisy training data files for " + track_name
                for repeat in range(1,10): # repeat this 9 times to create 9 new noisy files  
                    csv_file = open(output_folder + "/" + track_name + "_" + str(repeat) + ".csv" ,'w')
                    make_csv_file(csv_file, data_file, "normal")
                csv_file = open(output_folder + "/" + track_name + "_10" + ".csv" ,'w')
                make_csv_file(csv_file, data_file, "original")
                csv_file.close()            

def generate_test_and_training_files(DATA_FOLDER, DATA_OUPUT_FOLDER):
    training_data = []
    test_data = []
    output_folder = DATA_OUPUT_FOLDER + "Test_and_Training_data_file" + datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    #os.makedirs(output_folder)
    for root, dirs, files in os.walk(DATA_FOLDER):
        for file in files:
            if file.endswith(".dat"):
                filename = root + "/" + file
                input_data_file = open(filename, 'r')
                # skip first two lines because they are headers 
                raw_data_file = input_data_file.readlines()
                data_headers = raw_data_file[:2]
                data_file = raw_data_file[2:]
                for repeat in range(0,9):
                    shuffle(data_file)
                    training_data_index = int(len(data_file)*0.9)
                    training_data.extend(data_file[0:training_data_index])
                    test_data.extend(data_file[training_data_index:])

    #csv_train = open(output_folder + "/training_set"  + ".csv" ,'w')
    #csv_test= open(output_folder + "/test_set"  + ".csv" ,'w')
    make_csv_file(None, training_data, "normal")
    make_csv_file(None, test_data, "normal")
    csv_train.close()
    csv_test.close()



def make_csv_file(csv_file, data_file, noise_distribution):
    counter = 0
    for line in data_file:
        data_array = line.split(";")
        input_vector = data_array[0:len(data_array)-5]
        target_vector = data_array[len(data_array)-5:]
        noisy_data = created_noisy_data(input_vector, noise_distribution)
        new_data_vector = np.append(noisy_data, target_vector).tolist()
        data_string =  ';'.join(map(str, new_data_vector))
        counter += 1
        print data_string
        if counter >= 1000:
            sys.exit()
        #csv_file.write(data_string)
    
if __name__ == "__main__":
    if not os.path.exists(DATA_OUPUT_FOLDER): # make output folder if it not exists yet 
        os.makedirs(DATA_OUPUT_FOLDER)
    if  GENERATE_SEPARATED_DATA_FILES:
        generate_separated_data_files(DATA_FOLDER, DATA_OUPUT_FOLDER)
    if GENERATE_TEST_AND_TRAINING_FILES:
        generate_test_and_training_files(DATA_FOLDER, DATA_OUPUT_FOLDER) 