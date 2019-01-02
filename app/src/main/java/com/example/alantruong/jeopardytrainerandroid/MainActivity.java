package com.example.alantruong.jeopardytrainerandroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Jeopardy Trainer");

        setContentView(R.layout.activity_main);
        mTextMessage = (TextView) findViewById(R.id.message);


        final Button startButton = findViewById(R.id.startButton);
        final TextView questionTextView = findViewById(R.id.questionTextView);
        final TextView answerTextView = findViewById(R.id.answerTextView);
        final TextView categoryTextView = findViewById(R.id.categoryTextView);


        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Ion.with(getApplicationContext()).load("http://www.j-archive.com/showgame.php?game_id=6181").asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        try {
                            DocumentBuilder builder = dbFactory.newDocumentBuilder();
                            Document doc = builder.parse(new InputSource(new StringReader(result)));
                            doc.getDocumentElement().normalize();
                            XPath xpath = XPathFactory.newInstance().newXPath();
                            XPathExpression xExpress = xpath.compile("//div[@id='jeopardy_round']//td[@class='category_name']");
                            NodeList nl = (NodeList) xExpress.evaluate(doc, XPathConstants.NODESET);

                            String category1 = nl.item(0).getTextContent();
                            String category2 = nl.item(1).getTextContent();
                            String category3 = nl.item(2).getTextContent();
                            String category4 = nl.item(3).getTextContent();
                            String category5 = nl.item(4).getTextContent();
                            String category6 = nl.item(5).getTextContent();
                            categoryTextView.setText(category6);

                            //clue order number
                            String clueOrderNumberXpath = "//div[@id='jeopardy_round']//td[. ='1']";
                            xExpress = xpath.compile(clueOrderNumberXpath);

                            //clue value ($)
                            xExpress = xpath.compile(clueOrderNumberXpath + "/parent::tr/td[@class='clue_value']");

                            //clue text (the question)
                            xExpress = xpath.compile(clueOrderNumberXpath + "/ancestor::td[@class='clue']//td[@class='clue_text']");

                            //the answer (solution)
                            //use get the "onMouseOver" attribute and then get the string that in between class="correct_response"> and </em>
                            xExpress = xpath.compile(clueOrderNumberXpath + "/ancestor::div[1]");






                        }
                        catch (Exception p)
                        {

                        }
                    }
                });
            }
        });
    }



}
