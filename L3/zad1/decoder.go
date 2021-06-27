package main

import (
	"fmt"
	"strings"
)

func reverse_bit_stuffing(s string) string {
	var output strings.Builder
	one_counter := 0
	for _, c := range s {
		if c == '1' {
			one_counter += 1
			output.WriteRune(c)
		} else {
			if one_counter < BITS_STUFFING_LENGTH {
				output.WriteRune(c)
			}
			one_counter = 0
		}
	}
	return output.String()
}

func decode_frame(input_file, output_file string) {
	var toReturn string
	s := read_file(input_file)

	decoded := reverse_bit_stuffing(s[len(PREAMBLE) : len(s)-len(PREAMBLE)])
	data := decoded[:len(decoded)-CRC_SIZE]
	crc := decoded[len(decoded)-CRC_SIZE:]
	calculated_crc := calculate_crc(data)

	fmt.Println("DECODING: ", s)
	fmt.Println("After preamble deletion and unstuffing: ", decoded)
	fmt.Println("Data: ", data)
	fmt.Println("CRC from decoded: ", crc)
	fmt.Println("Calculated CRC for data", calculated_crc)
	if s[:len(PREAMBLE)] == PREAMBLE && s[len(s)-len(PREAMBLE):] == PREAMBLE {
		if calculated_crc == crc {
			toReturn = data
		} else {
			toReturn = "CRC_CODE does't match"
		}
	} else {
		toReturn = "Not proper preamble found"
	}
	fmt.Println("Decoded data: ", toReturn)
	write_to_file(output_file, toReturn)
}
