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


"""

Function that adds noise on a data_vector 
params: data_array and distribution of the noise
:return: An noisy version of the origiginal input data.
:rtype: array

"""

def created_noisy_data(data_array, noise_distribution):
    std = 1 # standard deviation of the noise 
    if noise_distribution == "normal":
    		noise_array = np.random.normal(data_array,std)
	# TODO: do we need more distributions ?
    return noise_array 


if __name__ == "__main__":
	
	DATA_FOLDER = "/Users/Maurits/Documents/UvA AI/Computational Intelligene /torcs data project/data"
	DATA_OUPUT_FOLDER = "/Users/Maurits/Documents/UvA AI/Computational Intelligene /torcs data project/data/data_output"
	
	
	if not os.path.exists(DATA_OUPUT_FOLDER): # make output folder if it not exists yet 
		os.makedirs(DATA_OUPUT_FOLDER)


	test_file = "/Users/Maurits/Documents/UvA AI/Computational Intelligene /torcs data project/data/data input/Road Tracks/torcsRace_CG-track-2.dat"
	raw_data_file =  open(test_file, 'r')
	
	# skip first two lines because they are headers
	# for every folder make e new data folder
	# for every track make a new folder
	# for every file make a csv file 
	"""
	data_file = raw_data_file.readlines()[2:]
	for line in data_file:
		data_array = line.split(";")
		input_vector = data_array[0:len(data_array)-5]
		target_vector = data_array[len(data_array)-5:]
		noisy_data = created_noisy_data(input_vector,"normal")
		new_data_vector = noisy_data.extend(target_vector)
	""" 

 
	for root, dirs, files in os.walk(DATA_FOLDER):
		for file in files:
            if file.endswith(".dat"):
                for line in data_file:
							data_array = line.split(";")
							# last 5 indexes of the data array are the target values 
							input_vector = data_array[0:len(data_array-5)]
							target_vector = data_array[len(data_array-5):]
							noisy_data = created_noisy_data(input_vector,"normal")
							new_data_vector = noisy_data.extend(target_vector)
						
  				""" 			

        	 	
	       	 
    
