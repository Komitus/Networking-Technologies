package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"
	"text/template"
)

func main() {
	//to be able to listen on wireshark we need to listen to loopback interface (127.0.0.1)
	//EX3
	http.HandleFunc("/request-header", handleHeader)
	//EX4
	http.HandleFunc("/view/html/", serveTemplate)
	fs := http.FileServer(http.Dir("./view"))
	http.Handle("/view/", http.StripPrefix("/view/", fs))
	//http.HandleFunc("/view/", viewHandler)  -- other method
	http.ListenAndServe(":8080", nil)
}

//EX3
func handleHeader(w http.ResponseWriter, r *http.Request) {
	r.Header.Add("Moj klucz", "moja wartosc")
	r.Header.Write(w)
}

//EX4
type Page struct {
	TextInside []byte
}

func loadPage(filename string) (*Page, error) {
	fmt.Println(filename)
	content, err := ioutil.ReadFile(filename)
	if err != nil {
		return nil, err
	}
	return &Page{TextInside: content}, nil
}

func viewHandler(w http.ResponseWriter, r *http.Request) {
	title := r.URL.Path
	fmt.Println(title)
	if strings.Contains(title, "img") || strings.Contains(title, "js") {
		//w.WriteHeader(http.StatusNotFound)
		return
	}
	p, _ := loadPage(title)
	if p != nil {
		w.Write(p.TextInside)
	}
}

func serveTemplate(w http.ResponseWriter, r *http.Request) {
	lp := "." + r.URL.Path
	fp := "./view/html/navbar.html"
	index := strings.LastIndex(lp, "/") + 1
	name := lp[index:]
	if strings.Contains(name, ".html") {
		fmt.Println(name)
		tmpl, _ := template.ParseFiles(lp, fp)
		tmpl.ExecuteTemplate(w, name, nil)
	}
}
