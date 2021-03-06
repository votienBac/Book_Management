package controller;
import model.Book;
import model.BookReaderManagement;
import model.Reader;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class DataController {
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private PrintWriter printWriter;
    private Scanner scanner;

    public void openFileToWrite(String fileName) {
        try {
            fileWriter = new FileWriter(fileName, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            printWriter = new PrintWriter(bufferedWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openFileToRead(String fileName){
        try {
            File file = new File(fileName);
            if(!file.exists()){
                file.createNewFile();// neu file chua ton tai thi tao moi file
            }
            scanner = new Scanner(Paths.get(fileName), "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void closeFileAfterRead(String fileName){
        try {
            scanner.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeBookToFile(Book book, String fileName) {
        openFileToWrite(fileName);
        printWriter.println(book.getBookID() + "|" + book.getBookName() + "|"
                + book.getAuthor() + "|" + book.getSpecialization() + "|"
                + book.getPublishYear() + "|" + book.getQuantity());
        closeFileAfterWrite(fileName);
    }

    public void writeReaderToFile(Reader reader, String fileName) {
        openFileToWrite(fileName);
        printWriter.println(reader.getReaderID() + "|" + reader.getFullName() + "|"
                + reader.getAddress() + "|" + reader.getPhoneNumber());
        closeFileAfterWrite(fileName);
    }

    public void writeBRMToFile(BookReaderManagement brm, String fileName) {
        openFileToWrite(fileName);
        printWriter.println(brm.getReader().getReaderID() + "|" + brm.getBook().getBookID()
                + "|" + brm.getNumOfBorrow() + "|" + brm.getState());
        closeFileAfterWrite(fileName);
    }

    public void closeFileAfterWrite(String fileName) {
        try {
            printWriter.close();
            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Reader> readReadersFromFile(String fileName){
        openFileToRead(fileName);
        ArrayList<Reader> readers = new ArrayList<>();
        while (scanner.hasNextLine()){
            String data = scanner.nextLine();
            Reader reader = createReaderFromData(data);
            readers.add(reader);
        }

        closeFileAfterRead(fileName);
        return readers;
    }

    public Reader createReaderFromData(String data) {
        String[] datas = data.split("\\|");
        //printWriter.println(reader.getReaderID() + "|" + reader.getFullName() + "|"
        //                + reader.getAddress() + "|" + reader.getPhoneNumber());
        //Reader(int readerID, String fullName, String address, String phoneNumber)
        Reader reader = new Reader(Integer.parseInt(datas[0]), datas[1], datas[2], datas[3]);
        return reader;
    }

    public ArrayList<Book> readBooksFromFile(String fileName){
        openFileToRead(fileName);
        ArrayList<Book> books = new ArrayList<>();
        while (scanner.hasNextLine()){
            String data = scanner.nextLine();
            Book book = createBookFromData(data);
            books.add(book);
        }
        closeFileAfterRead(fileName);
        return books;
    }

    public Book createBookFromData(String data) {
        String[] datas = data.split("\\|");
        //println(book.getBookID() + "|" + book.getBookName() + "|"
        //                + book.getAuthor() + "|" + book.getSpecialization() + "|"
        //                + book.getPublishYear() + book.getQuantity());

        //Book(int bookID, String bookName, String author,
        //                String specialization, int publishYear, int quantity)
        Book book = new Book(Integer.parseInt(datas[0]), datas[1],datas[2], datas[3],
                Integer.parseInt(datas[4]), Integer.parseInt(datas[5]));

        return book;
    }

    public ArrayList<BookReaderManagement> readBRMsFromFile(String fileName){
        var books= readBooksFromFile("BOOK.DAT");
        var readers= readReadersFromFile("READER.DAT");

        openFileToRead(fileName);
        ArrayList<BookReaderManagement> brms = new ArrayList<>();
        while (scanner.hasNextLine()){
            String data = scanner.nextLine();
            BookReaderManagement reader = createBRMFromData(data, readers, books);
            brms.add(reader);
        }

        closeFileAfterRead(fileName);
        return brms;
    }

    public BookReaderManagement createBRMFromData(String data, ArrayList<Reader> readers, ArrayList<Book> boooks) {
        String[] datas = data.split("\\|");
        //brm.getReader().getReaderID() + "|" + brm.getBook().getBookID()
        //                + "|" + brm.getNumOfBorrow() + "|" + brm.getState()

        //Book book, Reader reader,
        //                         int numOfBorrow, String state, int totalBorrowed
        BookReaderManagement brm =
                new BookReaderManagement(getBook(boooks, Integer.parseInt(datas[1])),
                        getReader(readers ,Integer.parseInt(datas[0])),
                        Integer.parseInt(datas[2]), datas[3], 0);
        return brm;
    }
    public void updateBRMFile(ArrayList<BookReaderManagement> list, String fileName){
        // xo?? b??? file c??
        File file = new File(fileName);
        if(file.exists()){
            file.delete();//xoa no di
            
        }
        //ghi moi file
        openFileToWrite(fileName);
        for (var brm: list) {
            printWriter.println(brm.getReader().getReaderID() + "|" + brm.getBook().getBookID()
                    + "|" + brm.getNumOfBorrow() + "|" + brm.getState());
            
        }
        closeFileAfterWrite(fileName);
    }
    private static Reader getReader(ArrayList<Reader> readers, int readerID) {
        for (int i=0; i<readers.size();i++) {
            if(readers.get(i).getReaderID()==readerID){
                return readers.get(i);
            }
        }
        return null;
    }

    private static Book getBook(ArrayList<Book> books, int bookID) {
        for (int i=0; i<books.size();i++) {
            if(books.get(i).getBookID()==bookID){
                return books.get(i);
            }
        }
        return null;
    }

    /*
    Ghi th??ng tin s??ch v??o file
    Ghi th??ng tin b???n ?????c v??o file
    Ghi th??ng tin BRM v??o file

    ?????c th??ng tin s??ch t??? file  --> chuy???n th??nh ?????i t?????ng s??ch
                                --> th??m v??o danh s??ch c??c s??ch
                                --> tr??? v??? danh s??ch c??c s??ch.

    ?????c th??ng tin b???n ?????c t??? file--> chuy???n th??nh ?????i t?????ng b???n ?????c
                                 --> th??m v??o danh s??ch c??c b???n ?????c
                                 --> tr??? v??? danh s??ch c??c b???n ?????c.

    ?????c th??ng tin BRM t??? file.  --> chuy???n th??nh ?????i t?????ng BRM
                                --> th??m v??o danh s??ch c??c BRM
                                --> tr??? v??? danh s??ch c??c BRM.
    Quy ?????c ghi th??ng tin:
        - M???i ?????i t?????ng ghi tr??n m???t d??ng
        - Gi???a c??c thu???c t??nh c??ch nhau b???i d???u |
    V?? d???: l???p Reader(readerID, fullName, address, phoneNumber)
    th?? th??ng tin trong file s??? l??: readerID|fullName|address|phoneNumber
     */
}
