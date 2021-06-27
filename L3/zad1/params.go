package main

import (
	"io/ioutil"
)

var BITS_STUFFING_LENGTH int = 5

//normally should be : preamble 7 times and SFD section 1010101010101010101010101010101010101010101010101010101010101011
//but for test and functionality of bit_stuffing i put 1 octet
//01111110
var PREAMBLE string = "01111110"
var CRC_SIZE int = 32

func check(e error) {
	if e != nil {
		panic(e)
	}
}

func read_file(file_path string) string {
	dat, err := ioutil.ReadFile(file_path)
	check(err)
	return string(dat)
}

func write_to_file(file_path, data string) {

	err := ioutil.WriteFile(file_path, []byte(data), 0644)
	check(err)
}
