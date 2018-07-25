package com.winfo.gdmsaec.app.databasepro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.winfo.gdmsaec.app.databasepro.bean.User;
import com.winfo.gdmsaec.app.databasepro.sqlite.BaseDao;
import com.winfo.gdmsaec.app.databasepro.sqlite.BaseDaoFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
        baseDao.insert(new User(1,"xiaolu","123456"));
        Toast.makeText(MainActivity.this, "执行成功.", Toast.LENGTH_SHORT).show();
    }
}
