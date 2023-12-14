# Java_code_project

****I wrote this code for an assignment as part of an AI course at the Universitat Politecnica de Catalunya in Barcelona, during my exchange. The course was taught in Spanish, which is why some terms are in Spanish. Most of them are pretty straightforward  but a few important ones that might be good to know are:

**Spanish		English**
Central		Power Plant
Cliente		Client
Tipo			Type
Tamaño		size
Garantizado	Guaranteed
Consumo		Consumption 

Below you can read the English translation of the original assignment. The main idea is implementing a heuristics, hillclimbing algorithm to solve an NP complete problem of distribution of clients over power plants.
To run it, simply run the Main.java file from the src folder. It will first give the results from the initial basic solution, then run the hillclimbing algorithm and show the results after hillclimbing.
The code is about exactly a year old. I have some comments myself about things I think I could have improved which I am happy to discuss during the interview, but overall I think it’s a good example of a fairly big piece of code, using OOP and algorithms in Java. 
****
The new European regulations for the energy market make the relationship between energy producers and consumers more direct and certain opportunities arise to optimize supply and demand. Each electricity generation company manages a park of power plants of different types that allow a certain number of megawatts to be produced daily. Since their capacity to produce electricity generally exceeds possible demand, it is not economical to keep all plants producing continuously. This means that they have to make the decision of which power plants to use and which power plants to stop, depending on the contracted demand. There are plants of different types, with different productions and daily costs in operation and in shutdown. 
Within electricity consumers is the group of large consumers. These can consume enough electricity to negotiate different rates depending on your needs. To also make consumption and production more flexible, there are tariffs that ensure supply in any circumstance and others, on the contrary, can leave the contracted supply without if there is an excess of external demand. This also implies a change in the rate price that these users have to pay. In the event that a customer with a non-guaranteed contract does not receive supply one day, they will receive compensation based on the consumption indicated in their contract. 
We will assume that we are going to solve the problem for a single electricity supplier, which has plants of 3 types (A, B, C). Each type of plant is in a megawatt production range and has daily costs depending on its type and production. The following table shows the values that each type of control unit can take:

Type		Production(Mw)	Running Cost		Stopping Cost
A		      250 to 750 		Prod*50+20000		15000
B		      100 to 250 		Prod*80+10000 	5000
C		      10 t0 100		  Prod*150+5 		  1500

When a plant is running, the cost includes all the electricity that can be generated. If all is not served, that electricity is lost, but we must bear the cost of producing it. 
We will have a set of large consumers divided into three groups according to their consumption: extra large, very large and large (XG, MG, G). Each consumer in his contract also determines what is the priority with which he wants to be served. We will have two priorities: guaranteed service and non-guaranteed service. The following table shows the prices depending on the characteristics of the consumer and his priority: 

Customer 	Consumption (Mw)	Guaranteed 	  Not Guaranteed 	Compensation 
XG 		    5 a 20  			    400 euros/Mw 	300 euros/Mw 	  50 euros/Mw 
MG 		    2 a 5 	 		  	  500 euros/Mw 	400 euros/Mw 	  50 euros/Mw 
G 		    1 a 2 				    600 euros/Mw 	500 euros/Mw 	  50 euros/Mw 

Each center is located at coordinates (x,y). We will assume that we have a square area of 100×100 kilometers and that the coordinates are whole numbers in kilometers. Clients will also have coordinates. Since the transmission of electricity over long distances is at a loss, the actual electricity to be supplied to the customer to fulfill the contract will be greater than the customer's demand. The following table shows the loss of supply as a function of distance: 

Distance 		      loss  
up to 10km		    0%
Up to 25km 		    10% 
Up to 50 Km 	    20% 
Up to 75 Km 	    40% 
More than 75 km   60% 

The problem 
We want to solve the problem for a specific day. For this we will have the list of power plants that the company has with its type, its 
coordinates and the production that it can generate in one day. We will also have the list of clients in which we will have their type, their priority,  their coordinates and their demand of the day. 
A solution will determine for each exchange which clients are assigned to it. The goal is to allocate customers in a way that maximizes profit. 
Solution Criteria 
To obtain and evaluate the solution we will use the following criteria and restrictions: 
1. A plant cannot be assigned more demand than it can produce. 
2. If a plant is running, it generates all its production, the one that is not assigned to a client is lost. 
3. A client is only assigned to one PBX. 
4. Customers are always fully served, that is, an allocation cannot be made if all customer demand cannot be served. 
5. All customers who have contracted guaranteed service must be served. 
6. The profit obtained must be maximized. 
You should think carefully about the heuristic function that indicates the benefit obtained. 





