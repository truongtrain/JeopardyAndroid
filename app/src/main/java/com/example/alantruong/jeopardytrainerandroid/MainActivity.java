package com.example.alantruong.jeopardytrainerandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
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
    //private DocumentBuilder builder;
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
    private Button recentButton;
    private Button randomButton;
    private Button findShowButton;
    private Button submitDateButton;
    private TextView scoreTextView;
    private EditText wagerEditText;
    private DatePicker datePicker;
    private int score = 0;
    private int clueNumber = 1;
    private int wager;
    private boolean isDailyDouble = false;
    private boolean isRound2 = false;
    private boolean isFinalJeopardy = false;
    private String showUrl;
    private String url;
    private String date;
    private int showIndex = 0; //0 means recent, 1 means random

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

    //go back to home screen
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Jeopardy Trainer");
        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_main);
        mTextMessage = (TextView) findViewById(R.id.message);


        recentButton = findViewById(R.id.recentButton);
        randomButton = findViewById(R.id.randomButton);
        findShowButton = findViewById(R.id.findShowButton);
        correctButton = findViewById(R.id.correctButton);
        incorrectButton = findViewById(R.id.incorrectButton);
        noAnswerButton = findViewById(R.id.noAnswerButton);
        submitDateButton = findViewById(R.id.submitDateButton);
        answerTextView = findViewById(R.id.answerTextView);
        clueTextView = findViewById(R.id.questionTextView);
        showAnswerButton = findViewById(R.id.showAnswerButton);
        scoreTextView = findViewById(R.id.scoreTextView);
        wagerEditText = findViewById(R.id.wagerEditText);
        submitWagerButton = findViewById(R.id.submitWagerButton);
        datePicker = findViewById(R.id.datePicker);




        recentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clueTextView.setText("Loading the most recent game...");
                showIndex = 0;
                Ion.with(getApplicationContext()).load("http://www.j-archive.com").asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        loadHtml(result);

                    }
                });

            }
        });

        randomButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clueTextView.setText("Loading a random game...");
                showIndex = 1;
                Ion.with(getApplicationContext()).load("http://www.j-archive.com").asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        loadHtml(result);
                    }
                });
            }
        });

        findShowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datePicker.setVisibility(View.VISIBLE);
                submitDateButton.setVisibility(View.VISIBLE);
                recentButton.setVisibility(View.INVISIBLE);
                randomButton.setVisibility(View.INVISIBLE);
                findShowButton.setVisibility(View.INVISIBLE);

            }
        });

        submitDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                month += 1;
                date = year + "-" + month + "-" + day;
            }
        });

        showAnswerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAnswerButton.setVisibility(View.INVISIBLE);
                answerTextView.setText(answer.toUpperCase());
                answerTextView.setVisibility(View.VISIBLE);
                correctButton.setVisibility(View.VISIBLE);
                incorrectButton.setVisibility(View.VISIBLE);
                noAnswerButton.setVisibility(View.VISIBLE);

            }
        });

        submitWagerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    wager = Integer.parseInt(wagerEditText.getText().toString());
                } catch (Exception e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You must enter a wager",
                            Toast.LENGTH_SHORT);

                    toast.show();
                }
                if (wager < 5 && !isFinalJeopardy) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You must wager at least $5",
                            Toast.LENGTH_SHORT);

                    toast.show();
                } else if (isFinalJeopardy && wager < 0) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You must wager at least $0",
                            Toast.LENGTH_SHORT);

                    toast.show();
                }else if (score <= 1000 && wager > 1000) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You cannot wager more than $1000",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else if (wager > score && score > 1000) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You cannot wager more than your current score",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //proceed with the clue
                    closeKeyboard();
                    clueTextView.setText(clueText.toUpperCase());
                    showAnswerButton.setVisibility(View.VISIBLE);
                    submitWagerButton.setVisibility(View.INVISIBLE);
                    wagerEditText.setVisibility(View.INVISIBLE);
                }
            }
        });

        correctButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isDailyDouble) {
                    isDailyDouble = false;
                    score += wager;
                } else {
                    score += Integer.parseInt(clueValue.substring(1));
                }
                if (isFinalJeopardy) {
                    score += wager;
                    clueTextView.setText("Congratulations! You finished this game!");
                    correctButton.setVisibility(View.INVISIBLE);
                    incorrectButton.setVisibility(View.INVISIBLE);
                    noAnswerButton.setVisibility(View.INVISIBLE);
                    recentButton.setVisibility(View.VISIBLE);
                    randomButton.setVisibility(View.VISIBLE);
                    findShowButton.setVisibility(View.VISIBLE);
                } else {
                    goToNextClue();
                }


            }
        });

        incorrectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isDailyDouble) {
                    isDailyDouble = false;
                    score -= wager;
                } else {
                    score -= Integer.parseInt(clueValue.substring(1));
                }
                if (isFinalJeopardy) {
                    clueTextView.setText("Congratulations! You finished this game! Play again?");
                } else {
                    goToNextClue();
                }

            }
        });

        noAnswerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isDailyDouble) {
                    isDailyDouble = false;
                    score -= wager;
                }
                if (isFinalJeopardy) {
                    clueTextView.setText("Congratulations! You finished this game! Play again?");
                } else {
                    goToNextClue();
                }
            }
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadHtml(String result) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(result)));
            doc.getDocumentElement().normalize();
            xpath = XPathFactory.newInstance().newXPath();
            xExpress = xpath.compile("//a[contains(.,'from show')]");
            NodeList shows = (NodeList) xExpress.evaluate(doc, XPathConstants.NODESET);
            int showsSize = shows.getLength();
            showUrl = ((Element) shows.item(showIndex)).getAttribute("href");
            String showInfo = ((Element) shows.item(showIndex)).getTextContent();
            String airDate = showInfo.substring(showInfo.length()-10, showInfo.length());
            String episodeNumber = showInfo.substring(11, 15);
            url = "http://www.j-archive.com/" + showUrl;
            clueTextView.setText("Episode " + episodeNumber + "\n Originally aired " + airDate);
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Ion.with(getApplicationContext()).load(url).asString().setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        initializeGame(result);
                                    }
                                });
                            }
                        });

                    } catch (Exception e) {

                    }
                }
            };
            t.start();

        } catch (Exception p) {
            p.printStackTrace();
        }
    }


    private void initializeGame(String result) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            recentButton.setVisibility(View.INVISIBLE);
            randomButton.setVisibility(View.INVISIBLE);
            findShowButton.setVisibility(View.INVISIBLE);
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            builder = dbFactory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(result)));
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
            showClue();
        } catch (Exception p) {
            p.printStackTrace();
        }
    }

    private void goToNextClue() {
        correctButton.setVisibility(View.INVISIBLE);
        incorrectButton.setVisibility(View.INVISIBLE);
        noAnswerButton.setVisibility(View.INVISIBLE);
        scoreTextView.setText("$" + score);
        answerTextView.setVisibility(View.INVISIBLE);
        clueNumber += 1;
        showClue();
    }

    private void showClue() {
        String clueOrderNumberXpath;
        if (!isRound2) {
            clueOrderNumberXpath = "//div[@id='jeopardy_round']//td[. ='" + clueNumber + "']";
        } else {
            clueOrderNumberXpath = "//div[@id='double_jeopardy_round']//td[. ='" + clueNumber + "']";
        }

        try {
            //clue value ($)
            xpath = XPathFactory.newInstance().newXPath();
            try {
                xExpress = xpath.compile(clueOrderNumberXpath + "/parent::tr/td[@class='clue_value']");
                clueValue = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0).getTextContent();
            } catch (Exception e) {
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
                    if (!isRound2) {
                        cluePosition = cluePosition.substring(7);
                    } else {
                        cluePosition = cluePosition.substring(8);
                    }
                    char clueRow = cluePosition.charAt(2);
                    int clueValueInt = 0;
                    if (clueRow == '1') {
                        clueValueInt = 200;
                    } else if (clueRow == '2') {
                        clueValueInt = 400;
                    } else if (clueRow == '3') {
                        clueValueInt = 600;
                    } else if (clueRow == '4') {
                        clueValueInt = 800;
                    } else if (clueRow == '5') {
                        clueValueInt = 1000;
                    }
                    if (isRound2) {
                        clueValueInt *= 2;
                    }
                    clueValue = "$" + clueValueInt;
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
                                        clueTextView.setText("DAILY DOUBLE \n Enter your wager:");
                                        wagerEditText.setVisibility(View.VISIBLE);
                                        submitWagerButton.setVisibility(View.VISIBLE);
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                } catch (Exception d) {
                    //go to round 2
                    if (!isRound2) {
                        isRound2 = true;
                        clueNumber = 1;
                        clueTextView.setText("DOUBLE JEOPARDY ROUND");
                        Thread.sleep(2000);

                        Thread t2 = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                    xExpress = xpath.compile("//div[@id='double_jeopardy_round']//td[@class='category_name']");
                                    NodeList categories = (NodeList) xExpress.evaluate(doc, XPathConstants.NODESET);
                                    category1 = categories.item(0).getTextContent();
                                    category2 = categories.item(1).getTextContent();
                                    category3 = categories.item(2).getTextContent();
                                    category4 = categories.item(3).getTextContent();
                                    category5 = categories.item(4).getTextContent();
                                    category6 = categories.item(5).getTextContent();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showClue();
                                        }
                                    });

                                } catch (Exception e) {

                                }
                            }
                        };
                        t2.start();


                    } else {
                        //go to final jeopardy
                        clueTextView.setText("FINAL JEOPARDY");
                        isFinalJeopardy = true;
                        xExpress = xpath.compile("//div[@id='final_jeopardy_round']//td[@class='category_name']");
                        NodeList categories = (NodeList) xExpress.evaluate(doc, XPathConstants.NODESET);
                        category1 = categories.item(0).getTextContent(); //final category
                        //clue text (the question)
                        xExpress = xpath.compile( "//td[@id='clue_FJ']");
                        Node clueTextNode = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0);
                        clueText = clueTextNode.getTextContent();
                        //the answer (solution)
                        //use get the "onMouseOver" attribute and then get the string that in between class="correct_response"> and </em>
                        xExpress = xpath.compile("//div[@id='final_jeopardy_round']//div[1]");
                        Node onMouseOverNode = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0);
                        Element onMouseOverElement = (Element) onMouseOverNode;
                        String onMouseOverContent = onMouseOverElement.getAttribute("onmouseover");
                        int beginIndex = onMouseOverContent.indexOf("correct_response") + 19;
                        int endIndex = onMouseOverContent.indexOf("</em>");
                        answer = onMouseOverContent.substring(beginIndex, endIndex);
                        if (answer.contains("</i>") || answer.contains("</I>")) {
                            answer = answer.substring(3, answer.length() - 4);
                        }

                        Thread t = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            clueTextView.setText("The category is " + category1 +"\n Enter your wager:");
                                            wagerEditText.setVisibility(View.VISIBLE);
                                            submitWagerButton.setVisibility(View.VISIBLE);
                                        }
                                    });

                                } catch (Exception e) {

                                }
                            }
                        };
                        t.start();
                    }
                }
            }

            //clue text (the question)
            xExpress = xpath.compile(clueOrderNumberXpath + "/ancestor::td[@class='clue']//td[@class='clue_text']");
            Node clueTextNode = ((NodeList) xExpress.evaluate(doc, XPathConstants.NODESET)).item(0);
            clueText = clueTextNode.getTextContent();


            //determine clue category
            Element clueTextElement = (Element) clueTextNode;
            String cluePosition = clueTextElement.getAttribute("id");
            if (!isRound2) {
                cluePosition = cluePosition.substring(7);
            } else {
                cluePosition = cluePosition.substring(8);
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
                                if (!isDailyDouble) {
                                    clueTextView.setText(clueText.toUpperCase());
                                    showAnswerButton.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    } catch (Exception e) {
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
            if (answer.contains("</i>") || answer.contains("</I>")) {
                answer = answer.substring(3, answer.length() - 4);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showCategory(char clueColumn) {
        if (clueColumn == '1') {
            clueTextView.setText(category1 + " for " + clueValue);
        } else if (clueColumn == '2') {
            clueTextView.setText(category2 + " for " + clueValue);
        } else if (clueColumn == '3') {
            clueTextView.setText(category3 + " for " + clueValue);
        } else if (clueColumn == '4') {
            clueTextView.setText(category4 + " for " + clueValue);
        } else if (clueColumn == '5') {
            clueTextView.setText(category5 + " for " + clueValue);
        } else if (clueColumn == '6') {
            clueTextView.setText(category6 + " for " + clueValue);
        }
    }


}
