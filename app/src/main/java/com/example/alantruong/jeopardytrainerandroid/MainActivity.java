package com.example.alantruong.jeopardytrainerandroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
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
    private TextView answerTextView;
    private Button correctButton;
    private Button incorrectButton;
    private Button noAnswerButton;
    private Button showAnswerButton;
    private Button submitWagerButton;
    private TextView scoreTextView;
    private EditText wagerEditText;
    private int score = 0;
    private int clueNumber = 1;
    private int wager;
    private boolean isDailyDouble = false;

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
        correctButton = findViewById(R.id.correctButton);
        incorrectButton = findViewById(R.id.incorrectButton);
        noAnswerButton = findViewById(R.id.noAnswerButton);
        answerTextView = findViewById(R.id.answerTextView);
        clueTextView = findViewById(R.id.questionTextView);
        showAnswerButton = findViewById(R.id.showAnswerButton);
        scoreTextView = findViewById(R.id.scoreTextView);
        wagerEditText = findViewById(R.id.wagerEditText);
        submitWagerButton = findViewById(R.id.submitWagerButton);

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
                            showClue(1);
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

        submitWagerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                try {
                    wager = Integer.parseInt(wagerEditText.getText().toString());
                }
                catch (Exception e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You must enter a wager",
                            Toast.LENGTH_SHORT);

                    toast.show();
                }
                if (wager < 5)
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You must wager at least $5",
                            Toast.LENGTH_SHORT);

                    toast.show();
                }
                else if (wager > score)
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You cannot wager more than your current score",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    //proceed with daily double clue
                    clueTextView.setText(clueText.toUpperCase());
                    showAnswerButton.setVisibility(View.VISIBLE);
                    submitWagerButton.setVisibility(View.INVISIBLE);
                    wagerEditText.setVisibility(View.INVISIBLE);
                }
            }
        });

        correctButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (isDailyDouble) {
                    isDailyDouble = false;
                    score += wager;
                }
                else {
                    score += Integer.parseInt(clueValue.substring(1));
                }
                goToNextClue();

            }
        });

        incorrectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (isDailyDouble) {
                    isDailyDouble = false;
                    score -= wager;
                }
                else {
                    score -= Integer.parseInt(clueValue.substring(1));
                }
                goToNextClue();

            }
        });

        noAnswerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (isDailyDouble) {
                    isDailyDouble = false;
                    score -= wager;
                }
                goToNextClue();
            }
        });
    }

    public void goToNextClue() {
        scoreTextView.setText("$" + score);
        answerTextView.setVisibility(View.INVISIBLE);
        correctButton.setVisibility(View.INVISIBLE);
        incorrectButton.setVisibility(View.INVISIBLE);
        noAnswerButton.setVisibility(View.INVISIBLE);
        clueNumber += 1;
        showClue(clueNumber);
    }

    public void showClue(int clueNumber) {

        String clueOrderNumberXpath = "//div[@id='jeopardy_round']//td[. ='" + clueNumber + "']";

        try {
            //clue value ($)
            xpath = XPathFactory.newInstance().newXPath();
            try {
                xExpress = xpath.compile(clueOrderNumberXpath + "/parent::tr/td[@class='clue_value']");
                clueValue = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0).getTextContent();
            }
            catch (Exception e) {
                try {
                    //handle daily double
                    xExpress = xpath.compile(clueOrderNumberXpath + "/parent::tr/td[@class='clue_value_daily_double']");
                    clueValue = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0).getTextContent();
                    isDailyDouble = true;
                    //clue text
                    xExpress = xpath.compile(clueOrderNumberXpath + "/ancestor::td[@class='clue']//td[@class='clue_text']");
                    Node clueTextNode = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0);
                    clueText = clueTextNode.getTextContent();
                    //determine clue value and category
                    Element clueTextElement = (Element) clueTextNode;
                    String cluePosition = clueTextElement.getAttribute("id");
                    cluePosition = cluePosition.substring(7);
                    char clueRow = cluePosition.charAt(2);
                    if (clueRow == '1')
                    {
                        clueValue = "$200";
                    }
                    else if (clueRow == '2')
                    {
                        clueValue = "$400";
                    }
                    else if (clueRow == '3')
                    {
                        clueValue = "$600";
                    }
                    else if (clueRow == '4')
                    {
                        clueValue = "$800";
                    }
                    else if (clueRow == '5')
                    {
                        clueValue = "$1000";
                    }
                    char clueColumn = cluePosition.charAt(0);
                    showCategory(clueColumn);

                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        clueTextView.setText("DAILY DOUBLE \n \n How much will you wager?");
                                        wagerEditText.setVisibility(View.VISIBLE);
                                        submitWagerButton.setVisibility(View.VISIBLE);
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
                }
                catch (Exception d) {

                }
            }

            //clue text (the question)
            xExpress = xpath.compile(clueOrderNumberXpath + "/ancestor::td[@class='clue']//td[@class='clue_text']");
            Node clueTextNode = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0);
            clueText = clueTextNode.getTextContent();


            //determine clue category
            Element clueTextElement = (Element) clueTextNode;
            String cluePosition = clueTextElement.getAttribute("id");
            cluePosition = cluePosition.substring(7);
            char clueColumn = cluePosition.charAt(0);
            showCategory(clueColumn);

            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isDailyDouble) {
                                    clueTextView.setText(clueText.toUpperCase());
                                    showAnswerButton.setVisibility(View.VISIBLE);
                                }
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
            if (answer.contains("</i>") || answer.contains("</I>"))
            {
                answer = answer.substring(3, answer.length()-4);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void showCategory(char clueColumn)
    {
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
    }




}
