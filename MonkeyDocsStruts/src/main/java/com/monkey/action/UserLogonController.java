package com.monkey.action;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Map;

public class UserLogonController extends ActionSupport {
    public String execute() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response= ServletActionContext.getResponse();
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Expose-Headers","responsemsg,token");
        BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        Gson gson=new Gson();
        Map<String ,Object> map = gson.fromJson(responseStrBuilder.toString(),Map.class);
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn= DriverManager.getConnection("jdbc:mysql://106.54.101.125:3306/MonkeyDocDB","root","monkeydoc123");
        Statement st= conn.createStatement();
        Statement st2= conn.createStatement();
        String tel= (String) map.get("tel");
        String email= (String) map.get("email");
        String userName= (String) map.get("userName");
        String password= (String) map.get("password");
        String sql1="INSERT INTO User (tel,email,userName,password) VALUES (?,?,?,?)";
        String sql2="select * from User where tel=";
        String sql3="select * from User where email=";
        ResultSet res= st.executeQuery(sql2+tel);
        ResultSet res2= st2.executeQuery(sql3+"\""+email+"\"");
        boolean flag1=false;
        boolean flag2=false;
        if(!res.next())
            flag1=true;
        if(!res2.next())
            flag2=true;
        if(flag1&&flag2) {
            PreparedStatement ps = conn.prepareStatement(sql1);
            ps.setString(1, tel);
            ps.setString(2, email);
            ps.setString(3, userName);
            ps.setString(4, password);
            ps.execute();
            response.setHeader("responsemsg", "logon_succeed");
            return NONE;
        }
        else if(flag1==true &&flag2==false){
            response.setHeader("responsemsg", "email_has_been_logon");
            return NONE;
        }
        else if(flag1==false&&flag2==true){
            response.setHeader("responsemsg","phone_has_been_logon");
            return NONE;
        }
        else{
            response.setHeader("responsemsg", "both_have_been_logon");
            return NONE;
        }
    }
}
