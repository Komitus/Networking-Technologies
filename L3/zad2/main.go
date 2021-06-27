package main

import (
	"time"
)

func main() {

	simulation := crate_simulation(20)

	simulation.addNode('A', 2, 0.03)
	simulation.addNode('B', 15, 0.03)

	for {
		time.Sleep(200 * time.Millisecond)
		simulation.print_cable()
		simulation.iterate()
	}
}
