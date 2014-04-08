# Thesis
## What is this?
This is the code accompanying my master's thesis *Auctions in Multi-Agent Systems for the Dynamic Pick-up and Delivery Problem with Time Windows*. It implements a bunch of extensions on a basic auctioning communication paradigm for agents. Runs on top of *RinSim* and uses scenarios described in *Gendreau, Michel, et al. "A tabu search algorithm for a routing and container loading problem." Transportation Science 40.3 (2006): 342-350.*

## How to run
* Clone repository
* Checkout the rel1.0 tag: `git checkout rel1.0`
* Build and package: ``mvn clean compile assembly:single``
* Run the jar ``java -jar target/thesis-1.0-jar-with-dependencies.jar -o results``
	* This will result in a bunch of CSV files in the results/ directory
* Generating figures can be done by running **TODO**