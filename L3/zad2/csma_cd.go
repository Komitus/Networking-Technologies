package main

import (
	"fmt"
	"math"
	"math/rand"
	"time"
)

type Node struct {
	id                rune
	position          int
	emits             bool
	jam               bool
	timeout           int
	probability       float32
	collision_counter int
}

type Transmission struct {
	direction int
	id        rune
}

type CSMA_CD_simulation struct {
	cable      [][]Transmission
	nodes      []Node
	cable_size int
}

func crate_simulation(size int) *CSMA_CD_simulation {
	cable_size := size
	cable := make([][]Transmission, cable_size)

	for i := 0; i < cable_size; i++ {
		cable[i] = make([]Transmission, 0)
	}

	return &CSMA_CD_simulation{cable, make([]Node, 0), size}
}
func (simulation *CSMA_CD_simulation) addNode(id rune, position int, probability float32) {
	timeout := 0
	s := rand.NewSource(time.Now().Unix())
	r := rand.New(s)
	for r.Float32() > probability {
		timeout++
	}
	node := Node{id, position, false, false, timeout, probability, 0}
	simulation.nodes = append(simulation.nodes, node)
}

func (simulation *CSMA_CD_simulation) iterate() {

	s := rand.NewSource(time.Now().Unix())
	r := rand.New(s)

	new_cable_state := make([][]Transmission, len(simulation.cable))

	for i := 0; i < len(simulation.cable); i++ {
		for _, t := range simulation.cable[i] {

			switch t.direction {
			case -1:
				if i != 0 {
					new_cable_state[i-1] = append(new_cable_state[i-1], Transmission{-1, t.id})
				}
			case 0:
				if i != 0 {
					new_cable_state[i-1] = append(new_cable_state[i-1], Transmission{-1, t.id})
				}
				if i != len(simulation.cable)-1 {
					new_cable_state[i+1] = append(new_cable_state[i+1], Transmission{1, t.id})
				}
			case 1:
				if i < len(simulation.cable)-1 {
					new_cable_state[i+1] = append(new_cable_state[i+1], Transmission{1, t.id})
				}
			}

		}
	}

	simulation.cable = new_cable_state

	for i := range simulation.nodes {
		if !simulation.nodes[i].emits && simulation.nodes[i].timeout == 0 {
			if len(simulation.cable[simulation.nodes[i].position]) > 0 {
				simulation.nodes[i].timeout += len(simulation.cable) / 2

			} else {
				simulation.nodes[i].timeout = 2*len(simulation.cable) + 1
				simulation.nodes[i].emits = true
			}
		}

		if simulation.nodes[i].emits {
			simulation.cable[simulation.nodes[i].position] = append(simulation.cable[simulation.nodes[i].position], Transmission{0, simulation.nodes[i].id})
			if len(simulation.cable[simulation.nodes[i].position]) > 1 {
				simulation.nodes[i].jam = true
				simulation.cable[simulation.nodes[i].position] = append(simulation.cable[simulation.nodes[i].position], Transmission{0, 'j'})
			}
		}

		simulation.nodes[i].timeout--

		if simulation.nodes[i].emits && simulation.nodes[i].timeout == 0 {
			simulation.nodes[i].emits = false

			if simulation.nodes[i].jam {
				multiplay := simulation.nodes[i].collision_counter
				timeouts := make([]int, 0)
				if simulation.nodes[i].collision_counter > 10 {
					multiplay = 10
				} else if multiplay > 15 {
					return
				}
				for j := 0; j <= simulation.nodes[i].collision_counter; j++ {
					timeouts = append(timeouts, int(math.Pow(float64(2.0), float64(j))))
				}
				timeout_index := r.Intn(len(timeouts))
				simulation.nodes[i].timeout = simulation.cable_size * int(timeouts[timeout_index])
				simulation.nodes[i].collision_counter++
				simulation.nodes[i].jam = false
			} else {
				simulation.nodes[i].collision_counter = 0
				for r.Float32() > simulation.nodes[i].probability {
					simulation.nodes[i].timeout++
				}
			}
		}

	}

}

func (simulation *CSMA_CD_simulation) print_cable() {

	fmt.Println()

	for _, cable_cell := range simulation.cable {

		switch len(cable_cell) {
		case 1:
			color := 32 + cable_cell[0].id%5
			if cable_cell[0].id == 'j' {
				fmt.Print("\u001B[32mJ ") //collision
			} else {
				fmt.Printf("\u001B[%dm%c ", color, cable_cell[0].id)
			}

		case 0:
			fmt.Print("  ")
		default:
			jam := false
			for _, elem := range cable_cell {
				if elem.id == 'j' {
					jam = true
					break
				}
			}
			if jam {
				fmt.Print("\u001B[33m\u26A1") //jam
			} else {
				fmt.Print("\u001B[31m# ") //collision
			}

		}
	}

	fmt.Println()
}
