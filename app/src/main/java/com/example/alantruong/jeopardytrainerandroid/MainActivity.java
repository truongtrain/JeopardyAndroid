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
    private String answer;
    private String clueText;
    private DocumentBuilder builder;
    private Document doc;
    private XPath xpath;
    private XPathExpression xExpress;
    private String category1;
    private String category2;
    private String category3;
    private String category4;
    private String category5;
    private String category6;
    private String clueValue;
    private TextView clueTextView;
    private Button showAnswerButton;
    private TextView scoreTextView;
    private int score = 0;
    private int clueNumber = 1;


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
        final Button correctButton = findViewById(R.id.correctButton);
        final Button incorrectButton = findViewById(R.id.incorrectButton);
        final Button noAnswerButton = findViewById(R.id.noAnswerButton);
        final TextView answerTextView = findViewById(R.id.answerTextView);
        clueTextView = findViewById(R.id.questionTextView);
        showAnswerButton = findViewById(R.id.showAnswerButton);
        scoreTextView = findViewById(R.id.scoreTextView);




        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Ion.with(getApplicationContext()).load("http://www.j-archive.com/showgame.php?game_id=6181").asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        try {
                            startButton.setVisibility(View.INVISIBLE);
                            builder = dbFactory.newDocumentBuilder();
                            doc = builder.parse(new InputSource(new StringReader(result)));
                            doc.getDocumentElement().normalize();
                            xpath = XPathFactory.newInstance().newXPath();
                            xExpress = xpath.compile("//div[@id='jeopardy_round']//td[@class='category_name']");
                            NodeList categories = (NodeList) xExpress.evaluate(doc, XPathConstants.NODESET);
                            category1 = categories.item(0).getTextContent();
                            category2 = categories.item(1).getTextContent();
                            category3 = categories.item(2).getTextContent();
                            category4 = categories.item(3).getTextContent();
                            category5 = categories.item(4).getTextContent();
                            category6 = categories.item(5).getTextContent();

                            //clue #1
                            String clueOrderNumberXpath = "//div[@id='jeopardy_round']//td[. ='1']";

                            //clue value ($)
                            xExpress = xpath.compile(clueOrderNumberXpath + "/parent::tr/td[@class='clue_value']");
                            clueValue = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0).getTextContent();

                            //clue text (the question)
                            xExpress = xpath.compile(clueOrderNumberXpath + "/ancestor::td[@class='clue']//td[@class='clue_text']");
                            Node clueTextNode = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0);
                            clueText = clueTextNode.getTextContent();

                            //determine clue category
                            Element clueTextElement = (Element) clueTextNode;
                            String cluePosition = clueTextElement.getAttribute("id");
                            cluePosition = cluePosition.substring(7);
                            char clueColumn = cluePosition.charAt(0);
                            if (clueColumn == '1')
                            {
                                clueTextView.setText(category1 + " for " + clueValue);
                            }
                            else if (clueColumn == '2')
                            {
                                clueTextView.setText(category2 + " for " + clueValue);
                            }
                            else if (clueColumn == '3')
                            {
                                clueTextView.setText(category3 + " for " + clueValue);
                            }
                            else if (clueColumn == '4')
                            {
                                clueTextView.setText(category4 + " for " + clueValue);
                            }
                            else if (clueColumn == '5')
                            {
                                clueTextView.setText(category5 + " for " + clueValue);
                            }
                            else if (clueColumn == '6')
                            {
                                clueTextView.setText(category6 + " for " + clueValue);
                            }

                            Thread t = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                clueTextView.setText(clueText.toUpperCase());
                                                showAnswerButton.setVisibility(View.VISIBLE);
                                            }
                                        });

                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            t.start();

                            //the answer (solution)
                            //use get the "onMouseOver" attribute and then get the string that in between class="correct_response"> and </em>
                            xExpress = xpath.compile(clueOrderNumberXpath + "/ancestor::div[1]");
                            Node onMouseOverNode = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0);
                            Element onMouseOverElement = (Element) onMouseOverNode;
                            String onMouseOverContent = onMouseOverElement.getAttribute("onmouseover");
                            int beginIndex = onMouseOverContent.indexOf("class=\"correct_response\">") + 25;
                            int endIndex = onMouseOverContent.indexOf("</em>");
                            answer = onMouseOverContent.substring(beginIndex, endIndex);


                        }
                        catch (Exception p)
                        {
                            p.printStackTrace();
                        }
                    }
                });
            }
        });


        showAnswerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                showAnswerButton.setVisibility(View.INVISIBLE);
                answerTextView.setText(answer.toUpperCase());
                answerTextView.setVisibility(View.VISIBLE);
                correctButton.setVisibility(View.VISIBLE);
                incorrectButton.setVisibility(View.VISIBLE);
                noAnswerButton.setVisibility(View.VISIBLE);

            }
        });

        correctButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                score += Integer.parseInt(clueValue.substring(1));
                scoreTextView.setText("$" + score);
                answerTextView.setVisibility(View.INVISIBLE);
                clueNumber += 1;
                showClue(clueNumber);
            }
        });
    }

    public void showClue(int clueNumber) {

        String clueOrderNumberXpath = "//div[@id='jeopardy_round']//td[. ='" + clueNumber + "']";

        try {


            //clue value ($)
            xpath = XPathFactory.newInstance().newXPath();
            xExpress = xpath.compile(clueOrderNumberXpath + "/parent::tr/td[@class='clue_value']");
            clueValue = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0).getTextContent();

            //clue text (the question)
            xExpress = xpath.compile(clueOrderNumberXpath + "/ancestor::td[@class='clue']//td[@class='clue_text']");
            Node clueTextNode = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0);
            clueText = clueTextNode.getTextContent();

            //determine clue category
            Element clueTextElement = (Element) clueTextNode;
            String cluePosition = clueTextElement.getAttribute("id");
            cluePosition = cluePosition.substring(7);
            char clueColumn = cluePosition.charAt(0);
            if (clueColumn == '1')
            {
                clueTextView.setText(category1 + " for " + clueValue);
            }
            else if (clueColumn == '2')
            {
                clueTextView.setText(category2 + " for " + clueValue);
            }
            else if (clueColumn == '3')
            {
                clueTextView.setText(category3 + " for " + clueValue);
            }
            else if (clueColumn == '4')
            {
                clueTextView.setText(category4 + " for " + clueValue);
            }
            else if (clueColumn == '5')
            {
                clueTextView.setText(category5 + " for " + clueValue);
            }
            else if (clueColumn == '6')
            {
                clueTextView.setText(category6 + " for " + clueValue);
            }


            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clueTextView.setText(clueText.toUpperCase());
                                showAnswerButton.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };
            t.start();

            //the answer (solution)
            //use get the "onMouseOver" attribute and then get the string that in between class="correct_response"> and </em>
            xExpress = xpath.compile(clueOrderNumberXpath + "/ancestor::div[1]");
            Node onMouseOverNode = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0);
            Element onMouseOverElement = (Element) onMouseOverNode;
            String onMouseOverContent = onMouseOverElement.getAttribute("onmouseover");
            int beginIndex = onMouseOverContent.indexOf("class=\"correct_response\">") + 25;
            int endIndex = onMouseOverContent.indexOf("</em>");
            answer = onMouseOverContent.substring(beginIndex, endIndex);


        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }




}
