package main

import (
	"fmt"
	"hash/crc32"
	"strconv"
	"strings"
)

func calculate_crc(s string) string {
	input := []byte(s)

	crc_code := crc32.ChecksumIEEE(input)
	crc_code_bits := strconv.FormatInt(int64(crc_code), 2)

	crc_lenght := CRC_SIZE - len(crc_code_bits)

	for i := 0; i < crc_lenght; i++ {
		crc_code_bits += "0"
	}
	//fmt.Println(len(crc_code_bits))
	return crc_code_bits
}
func bit_stuffing(s string) string {
	var output strings.Builder
	one_counter := 0
	for _, c := range s {
		if c == '1' {
			one_counter += 1
			output.WriteRune(c)
			if one_counter == BITS_STUFFING_LENGTH {
				output.WriteRune('0')
				one_counter = 0
			}
		} else {
			one_counter = 0
			output.WriteRune(c)
		}
	}
	return output.String()
}

func encode_frame(input_file, output_file string) {
	s := read_file(input_file)
	fmt.Println("Encoding: ", s)
	encoded := calculate_crc(s)
	fmt.Println("Calculated CRC: ", encoded)
	encoded = bit_stuffing(s + encoded)
	fmt.Println("After bit_stuffing (data and crc): ", encoded)
	encoded = PREAMBLE + encoded + PREAMBLE
	fmt.Println("Final result: ", encoded)
	fmt.Println()
	write_to_file(output_file, encoded)
}
