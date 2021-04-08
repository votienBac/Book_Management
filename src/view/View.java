package view;


import controller.ControllerUltility;
import controller.DataController;
import model.Book;
import model.BookReaderManagement;
import model.Reader;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

/*
    Viết menu cho phép thực hiện các chức năng 1, 2
 */
public class View {
    public static void main(String[] args) {
        int choice = 0;

        var booksFileName = "BOOK.DAT";
        var readersFileName = "READER.DAT";
        var controller = new DataController();
        var ultility= new ControllerUltility();
        var brmFileName = "BRM.DAT";

        var books = new ArrayList<Book>();
        var readers = new ArrayList<Reader>();
        var brms = new ArrayList<BookReaderManagement>();

        var isReaderChecked= false;
        var isBookChecked = false;

        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("_______________MENU_______________");
            System.out.println("1. Thêm một đầu sách vào file.");
            System.out.println("2. Hiển thị danh sách các sách có trong file.");
            System.out.println("3. Thêm một bạn đọc vào file.");
            System.out.println("4. Hiển thị bạn đọc có trong file.");
            System.out.println("5. Thông tin quản lí mượn.");
            System.out.println("6. Sắp xếp.");
            System.out.println("7. Tìm kiếm thông tin mượn theo tên bạn đọc.");
            System.out.println("0. Thoát khỏi ứng dụng.");
            System.out.println("Bạn chọn ? ");

            choice = scanner.nextInt();
            scanner.nextLine();// doc bo dong chua lua chon

            switch (choice) {
                case 0:
                    System.out.println("_____________________________________________");
                    System.out.println("Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!");
                    break;

                case 1:
                    if(!isBookChecked){
                        checkBookID(controller, booksFileName);
                        isBookChecked = true;
                    }
                    String[] specs = {"Science", "Art", "Economic", "IT"};
                    String bookName, author, spec;
                    int year, quan, sp;
                    System.out.println("Nhập tên sách: ");
                    bookName = scanner.nextLine();

                    System.out.println("Nhập tên tác giả: ");
                    author = scanner.nextLine();

                    do {
                        System.out.println("Nhập thể loại sách: ");
                        System.out.println("1. Science.\n2. Art.\n3. Economic.\n4. IT.");
                        sp = scanner.nextInt();
                    } while (sp < 1 || sp > 4);

                    spec = specs[sp - 1];

                    System.out.println("Nhập năm xuất bản: ");
                    year = scanner.nextInt();

                    System.out.println("Nhập số lượng: ");
                    quan = scanner.nextInt();
                    //public Book(int bookID, String bookName, String author,
                    //                String specialization, int publishYear, int quantity)
                    Book book = new Book(0, bookName, author, spec, year, quan);
                    controller.writeBookToFile(book, booksFileName);
                    break;

                case 2:
                    books = controller.readBooksFromFile(booksFileName);
                    showBookInfo(books);
                    break;
                case 3:
                    if (!isReaderChecked) {
                    checkReaderID(controller, readersFileName);
                    isReaderChecked = true;
                }
                    String fullName, address, phoneNum;
                    System.out.println("Nhập họ và tên: ");
                    fullName = scanner.nextLine();

                    System.out.println("Nhập địa chỉ: ");
                    address = scanner.nextLine();

                    do {
                        System.out.println("Nhập số điện thoại: ");
                        phoneNum = scanner.nextLine();
                    } while (!phoneNum.matches("\\d{10}"));

                    //Reader(int readerID, String fullName, String address, String phoneNumber)
                    Reader reader = new Reader(0, fullName, address, phoneNum);
                    controller.writeReaderToFile(reader, readersFileName);
                    break;



                case 4:
                    readers=controller.readReadersFromFile(readersFileName);
                    showReadersInFo(readers);

                    break;
                case 5:
                    // B0: Khoi tao
                    readers= controller.readReadersFromFile(readersFileName);
                    books = controller.readBooksFromFile(booksFileName);
                    brms= controller.readBRMsFromFile(brmFileName);
                    //B1:
                    int readerID, bookID;
                    boolean isBorrowable = false;
                    boolean maxBookBorrowable= false;
                    do{
                        showReadersInFo(readers);
                        System.out.println("_________________________________");
                        System.out.println("Nhập mã bạn đọc, nhập 0 để bỏ qua: ");
                        readerID=scanner.nextInt();
                        if(readerID==0){
                            break;//tat ca ban doc đã được mượn đủ sách quy định
                        }

                        isBorrowable= checkBorrowed( brms, readerID);
                        if(isBorrowable){
                            break;
                        }else
                            System.out.println(" Bạn đọc này đã mượn đủ số lượng cho phép.");
                    }while (true);

                    do {
                        showBookInfo(books);
                        System.out.println("_____________________________________");
                        System.out.println("Nhập mã sách cần mượn, nhập 0 để bỏ qua");
                        bookID = scanner.nextInt();
                        if (bookID == 0) {
                            break;
                        }

                        maxBookBorrowable = checkMaxBookBorrowed(brms, readerID, bookID);// true neu da muon du 3
                        if(maxBookBorrowable){
                            System.out.println("Vui lòng chọn đầu sách khác");
                        }else
                            break;
                    }while (true);

                    int total= getTotal(brms, readerID, bookID);
                    do{
                        System.out.println("Nhập đó lượng mượn, tối đa 3 cuốn (đã mượn "+total+"): ");
                        int x=scanner.nextInt();
                        if((x+total)<=3 && (x+total)>=1){
                            total+=x;
                            break;
                        }else System.out.println("Nhập quá số lượng quy định! Vui lòng nhập lại.");
                    }while (true);
                    scanner.nextLine();// đọc bỏ dòng có chứa số

                    System.out.println(" Nhập tình trạng: ");
                    String status="";
                    status= scanner.nextLine();

                    //B4 cap nhật lại file BRM.DAT

                    Book currentBook=getBook(books, bookID);
                    Reader currrentReader= getReader(readers, readerID);
                    BookReaderManagement b= new BookReaderManagement(currentBook,
                            currrentReader, total, status, 0);

                    //B5

                    brms=ultility.updateBRMInfo(brms, b);// cap nhat danh sach quan li muon
                    controller.updateBRMFile(brms, brmFileName);// cap nhat file du lieu

                    showBRMInfo(brms);

                    




                    break;
                case 6:
                    brms=controller.readBRMsFromFile(brmFileName);// doc ra sanh sach quan li

                    //update tong so luong muon
                    brms=ultility.updateTotalBorrow(brms);
                    System.out.println("___________________________________________" );
                    System.out.println(" _______________các lựa chọn sắp xếp________");
                    int x=0;

                    do{
                        System.out.println("1. Sắp xếp theo tên bạn dọc(A-Z).");
                        System.out.println("2. Sắp xếp theo tổng số lượng mượn(Giảm dần).: ");
                        System.out.println("0. Trở lại menu chính.");
                        System.out.println("Bạn chọn?");
                        x= scanner.nextInt();
                        if(x==0){
                            break;
                        }
                        switch (x){
                            case 1:
                                // sap xep theo ten
                                brms= ultility.sortByReaderName(brms);
                                showBRMInfo(brms);
                                break;
                            case 2:
                                //sap xep theo so luong muon
                                brms=ultility.sortByNumOfBorrow(brms);
                                showBRMInfo(brms);
                                break;
                        }

                    }while (true);
                    break;
                case 7:
                    brms= controller.readBRMsFromFile(brmFileName);
                    brms=ultility.updateTotalBorrow(brms);
                    System.out.println(" Nhập cụm từ có trong tên bạn đọc cần tìm: ");
                    String key= scanner.nextLine();
                    var result= ultility.searchByReaderName(brms, key);
                    if(result.size()==0){
                        System.out.println("Không tìm thấy bạn đọc!");
                    }else {
                        showBRMInfo(result);
                    }
                    break;
            }
        } while (choice != 0);
    }

    private static void showBRMInfo(ArrayList<BookReaderManagement> brms) {
        for (var b:brms) {
            System.out.println(b);

        }
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

    private static int getTotal(ArrayList<BookReaderManagement> brms, int readerID,
                                int bookID) {
        for (var r:brms) {
            if(r.getReader().getReaderID()==readerID && r.getBook().getBookID()==bookID){
                return  r.getNumOfBorrow();
            }
        }
        return 0;
    }

    private static boolean checkMaxBookBorrowed(ArrayList<BookReaderManagement> brms,
                                                int readerID ,int bookID) {
        int count=0;
        for (var r:brms) {
            if(r.getReader().getReaderID()==readerID
                    && r.getBook().getBookID()==bookID && r.getNumOfBorrow()>=3){
                return true;// ko ddc muon tiep dau sach nay
            }
        }
        return false;// dc muon tiep
    }

    private static boolean checkBorrowed(ArrayList<BookReaderManagement> brms, int readerID) {
        int count=0;
        for (var r: brms) {
            if(r.getReader().getReaderID()==readerID){
                count +=r.getNumOfBorrow();
            }

        }
        if(count>=15){
            return false;

        }
        return true;
    }

    private static void showReadersInFo(ArrayList<Reader> readers) {
        System.out.println("______________Thông tin các bạn đọc trong file______________");
        for (var r:readers) {
            System.out.println(r);
        }
    }

    private static void checkReaderID(DataController controller, String readersFileName) {
        var listReaders = controller.readReadersFromFile(readersFileName);
        if(listReaders.size()==0){

        }else {
            Reader.setId(listReaders.get(listReaders.size() - 1).getReaderID() + 1);
        }
    }

    private static void checkBookID(DataController controller, String fileName) {
        var listBooks = controller.readBooksFromFile(fileName);
        if(listBooks.size()==0){

        }else
        Book.setId(listBooks.get(listBooks.size()-1).getBookID() + 1);
    }

    private static void showBookInfo(ArrayList<Book> books) {
        System.out.println("_________________Thông tin sách trong file_________________");
        for (var b : books) {
            System.out.println(b);
        }

    }
}
