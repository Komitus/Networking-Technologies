package main

import "fmt"

func main() {
	encode_frame("input.txt", "output_encoder.txt")
	decode_frame("output_encoder.txt", "output_decoder.txt")

	input := read_file("input.txt")
	decoded := read_file("output_decoder.txt")

	if input != decoded {
		fmt.Println("Input and decode don't match")
	} else {
		fmt.Println("Match")
	}

}
