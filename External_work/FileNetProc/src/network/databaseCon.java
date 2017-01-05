package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class databaseCon {
	private String mDriverName = "com.mysql.jdbc.Driver";
	private String mDbName; // Database Name
	private String mUserName; // Database User Name
	private String mPassword; // Database Password
	private String mUrl = "jdbc:mysql://"; // Database URL
	private String mTableName = "test_image";
	private Connection mConnection;
	private PreparedStatement mPreSt;
	
	private ResultSet mResultSet = null;
	private Statement mStmt = null;
	
	public databaseCon(String Dbname, String UserName, String Password, String url){
		mDbName = Dbname;
		mUserName = UserName;
		mPassword = Password;
		mUrl = mUrl + url + ":3306/";
		
		try{
			Class.forName(mDriverName);
			mConnection = DriverManager.getConnection(mUrl + mDbName, mUserName, mPassword);	
			mStmt = mConnection.createStatement();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void createImageTable(){
		try {
			mStmt.executeUpdate("create table " + mTableName +" (" +
					"id integer primary key," +
					"filename varchar(50) not null," +
					"file mediumblob not null);");
		} catch (SQLException e) {
			System.out.println("SQLException : " + e.getMessage());
			System.out.println("SQLState : " + e.getSQLState());
			System.out.println("VendorError : " + e.getErrorCode());
			e.printStackTrace();
		}
	}
	
	public void insertImage(File image){
		int index = 0;

		try {
			FileInputStream fin = new FileInputStream(image);
			mPreSt = mConnection.prepareStatement("insert into " + mTableName +"(FILENAME, FILE) VALUES(?,?)");
			index = getCurrentID() + 1;
			
			System.out.println("Index : " + index);
			
			mPreSt.setString(1, image.getName());
			mPreSt.setBinaryStream(2,fin, (int) image.length());
			mPreSt.executeUpdate();
			System.out.println("Inserting Successfully");
			
			mPreSt.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("SQLException : " + e.getMessage());
			System.out.println("SQLState : " + e.getSQLState());
			System.out.println("VendorError : " + e.getErrorCode());
			e.printStackTrace();
		}
		
	}
	
	public void getImage(int id)
	{
		try {
			mPreSt = mConnection.prepareStatement("select file from " + mTableName +" where id = " + id + ";");
			mResultSet = mPreSt.executeQuery();
			
			mResultSet.next();
			InputStream inputStream = mResultSet.getBinaryStream(1);
			try {
				FileOutputStream fos = new FileOutputStream("test.jpg");
				int i = 0;
				
				while((i = inputStream.read()) != -1){
					fos.write(i);
				}
				
				fos.close();
				inputStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SQLException e) {
			System.out.println("SQLException : " + e.getMessage());
			System.out.println("SQLState : " + e.getSQLState());
			System.out.println("VendorError : " + e.getErrorCode());
			e.printStackTrace();
		}		
		finally{
			try {
				mPreSt.close();
				mResultSet = null;
			} catch (SQLException e) {
				System.out.println("SQLException : " + e.getMessage());
				System.out.println("SQLState : " + e.getSQLState());
				System.out.println("VendorError : " + e.getErrorCode());
				e.printStackTrace();
			}
		}
	}
	
	public void showTable(){
		try {
			mResultSet = mStmt.executeQuery("show tables");
			
			System.out.println("fetch size : " + mResultSet.getFetchSize());

			while(mResultSet.next()){
				System.out.println(mResultSet.getString(1) + "" + mResultSet.getString(2));
			}
		} catch (SQLException e) {
			System.out.println("SQLException : " + e.getMessage());
			System.out.println("SQLState : " + e.getSQLState());
			System.out.println("VendorError : " + e.getErrorCode());
			e.printStackTrace();
		}
		
	}
	
	public void dbClose(){
		try {
			mConnection.close();
		} catch (SQLException e) {
			System.out.println("SQLException : " + e.getMessage());
			System.out.println("SQLState : " + e.getSQLState());
			System.out.println("VendorError : " + e.getErrorCode());
			e.printStackTrace();
		}
	}
	
	private int getCurrentID(){
		try {
			mPreSt = mConnection.prepareStatement("select id from " + mTableName +" order by id desc;");
			mResultSet = mPreSt.executeQuery();
			if(!mResultSet.next()){
				return 0;
			}
			
			return Integer.parseInt(mResultSet.getString(1));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
}
