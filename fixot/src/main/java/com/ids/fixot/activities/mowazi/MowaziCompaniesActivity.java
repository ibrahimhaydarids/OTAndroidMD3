package com.ids.fixot.activities.mowazi;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.mowaziAdapters.MowaziCompanyRecyclerArrayAdapter;
import com.ids.fixot.adapters.mowaziAdapters.MowaziSectorSpinnerAdapter;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziSector;
import com.ids.fixot.model.mowazi.SimpleDividerItemDecoration;
import com.ids.fixot.parser.MowaziCompanyParser;
import com.ids.fixot.parser.MowaziSectorParser;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


/**
 * Created by Amal on 5/4/2016.
 */
public class MowaziCompaniesActivity extends AppCompatActivity implements MowaziCompanyRecyclerArrayAdapter.RecyclerViewOnItemClickListener {

    public LinearLayout llissue, llforbid, llmodification, llsymbol, llsector;
    public ImageView arrow, ivissue, ivforbid, ivsector, ivsymbol,
            ivmodification;
    EditText spinnerCompanies;
    ArrayList<MowaziCompany> companiestoSort = new ArrayList<MowaziCompany>();
    // private MowaziSharedPreference settings;
    TextView tvNoData;
    int LastCompanyId;
    private ArrayList<MowaziCompany> companies = new ArrayList<>();
    private ProgressBar progress;
    private MowaziCompanyRecyclerArrayAdapter adapter;
    private TextView title;
    private GetCompaniesBySector get;
    private RelativeLayout top2, top;
    private EditText searchtxt;
    private boolean search = false;
    private boolean flagLoading = false, pulltoRefresh = false,
            favorite = false;
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisiblesItems;
    private SwipeRefreshLayout swipeContainer;
    private LinearLayoutManager llm;
    private RecyclerView rvNews;
    private int sectorId = 0;
    private Spinner spinnerSectors;
    private ArrayList<MowaziSector> allSectors = new ArrayList<>();
    private MowaziSectorSpinnerAdapter sectorSpinnerAdapter;
    private RelativeLayout searchlayoutall, companyLayout, spinnerSectlayout;
    private RelativeLayout main;
    private LinearLayout bottom;
    private ImageButton back;
    private GetSectors getSectors;
    private GetCompaniesBySector getCompaniesBySector;
    private MowaziSector selectedsector = new MowaziSector();

    public MowaziCompaniesActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_mowazi_companies);


        // settings = new MowaziSharedPreference();

        progress = (ProgressBar) findViewById(R.id.progressbar);

        // companies.add(new Company());
        adapter = new MowaziCompanyRecyclerArrayAdapter(this, companies, this);
        //View view = View.inflate(MowaziCompaniesActivity.this, R.layout.company_list_header, null);

        title = (TextView) findViewById(R.id.title);
        top = findViewById(R.id.top);

        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        bottom = (LinearLayout) findViewById(R.id.bottom);
        /*if (MyApplication.lang.equals("en"))
            title.setTypeface(MyApplication.opensansbold);
        else
            title.setTypeface(MyApplication.droidbold);*/


        Log.wtf("fav", "" + favorite);
        llm = new LinearLayoutManager(MowaziCompaniesActivity.this);
        main = (RelativeLayout) findViewById(R.id.main_bar);
        back = (ImageButton) findViewById(R.id.back);
        rvNews = (RecyclerView) findViewById(R.id.rvDeals);
        searchlayoutall = (RelativeLayout) findViewById(R.id.searchlayoutall);
        companyLayout = (RelativeLayout) findViewById(R.id.companyLayout);
        spinnerSectlayout = (RelativeLayout) findViewById(R.id.spinnerSectlayout);
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        spinnerCompanies = (EditText) findViewById(R.id.spinnerCompanies);


        spinnerSectors = (Spinner) findViewById(R.id.spinnerSectors);
        MowaziSector sector = new MowaziSector();
        sector.setId(0);
        sector.setName("-- " + getResources().getString(R.string.sector) + " --");
        allSectors.add(sector);
        sectorSpinnerAdapter = new MowaziSectorSpinnerAdapter(MowaziCompaniesActivity.this, allSectors);
        spinnerSectors.setAdapter(sectorSpinnerAdapter);
        spinnerSectors.setSelection(0);
        rvNews.setLayoutManager(llm);
        rvNews.setAdapter(adapter);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setRefreshing(false);

        swipeContainer.setColorSchemeColors(0, 0, 0, 0);
        //swipeContainer.setProgressBackgroundColor(android.R.color.transparent);

        progress.setVisibility(View.VISIBLE);
        if (!favorite) {
            swipeContainer
                    .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                        public void onRefresh() {
                            swipeContainer.setRefreshing(false);
                            refreshItems();
                            pulltoRefresh = true;

                            swipeContainer.setRefreshing(false);
                        }
                    });
        }

        rvNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) // check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (flagLoading == false) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            flagLoading = true;
                            Log.wtf("will", "do loadmore");
                            if (!favorite)
                                additems();
                        }
                    } else {
                        Log.wtf("loading still", "shaghal");
                    }
                }
            }
        });

        swipeContainer.setRefreshing(false);

        spinnerCompanies
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            search(v);

                            return true;
                        }
                        return false;
                    }
                });


        spinnerSectors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView,
                                       View view, int position, long id) {

                if (position != 0) {
                    MowaziSector sector = sectorSpinnerAdapter
                            .getItem(position);
                    selectedsector = sector;
                } else
                    selectedsector.setId(0);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        rvNews.addItemDecoration(new SimpleDividerItemDecoration(this, 0,
                R.drawable.line_divider));
        InitializeHeader();
        GetSectors get = new GetSectors();
        get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //Actions.initializeToolBar(getString(R.string.mowazi_companies), MowaziCompaniesActivity.this);
        Actions.overrideFonts(this, companyLayout, true);

    }

    public void InitializeHeader() {
        llissue = (LinearLayout) findViewById(R.id.llissue);
        llforbid = (LinearLayout) findViewById(R.id.llforbid);
        llmodification = (LinearLayout) findViewById(R.id.llmodification);

        llsymbol = (LinearLayout) findViewById(R.id.llsymbol);
        llsector = (LinearLayout) findViewById(R.id.llsector);
        ivissue = (ImageView) findViewById(R.id.ivissue);
        ivforbid = (ImageView) findViewById(R.id.ivbid);
        ivsector = (ImageView) findViewById(R.id.ivsector);

        ivsymbol = (ImageView) findViewById(R.id.ivsymbol);
        ivmodification = (ImageView) findViewById(R.id.ivmodification);
        llsymbol.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                ivmodification.setImageDrawable(ContextCompat.getDrawable(
                        MowaziCompaniesActivity.this, R.drawable.updownempty));
                ivsector.setImageDrawable(ContextCompat.getDrawable(
                        MowaziCompaniesActivity.this, R.drawable.updownempty));

                MyApplication.editor.putString("button", "ivsymbol");
                if (ivsymbol.getTag().equals("desc")) {

                    ivsymbol.setTag("asce");
                    ivsymbol.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updown));
                    ivsymbol.setRotation(180);
                    sort("asce");
                    MyApplication.editor.putString("CompaniesSortKind", "symbol");
                    MyApplication.editor.putString("CompaniesSortType", "asce");

                    MyApplication.editor.apply();
                } else {
                    ivsymbol.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updown));
                    ivsymbol.setRotation(ivsymbol.getRotation() - 180);

                    ivsymbol.setTag("desc");
                    sort("desc");
                    MyApplication.editor.putString("CompaniesSortKind", "symbol");
                    MyApplication.editor.putString("CompaniesSortType", "desc");
                    MyApplication.editor.apply();
                }

            }
        });

        llsector.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                ivmodification.setImageDrawable(ContextCompat.getDrawable(
                        MowaziCompaniesActivity.this, R.drawable.updownempty));
                ivsymbol.setImageDrawable(ContextCompat.getDrawable(
                        MowaziCompaniesActivity.this, R.drawable.updownempty));

                MyApplication.editor.putString("button", "ivsector");
                if (ivsector.getTag().equals("desc")) {
                    ivsector.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updown));
                    ivsector.setRotation(180);
                    ivsector.setTag("asce");
                    sortSector("asce");
                    MyApplication.editor.putString("CompaniesSortKind", "sector");
                    MyApplication.editor.putString("CompaniesSortType", "asce");
                    MyApplication.editor.apply();
                } else {
                    ivsector.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updown));
                    ivsector.setRotation(ivsector.getRotation() - 180);

                    ivsector.setTag("desc");
                    sortSector("desc");
                    MyApplication.editor.putString("CompaniesSortKind", "sector");
                    MyApplication.editor.putString("CompaniesSortType", "desc");
                    MyApplication.editor.apply();
                }

            }
        });
        llmodification.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                ivsymbol.setImageDrawable(ContextCompat.getDrawable(
                        MowaziCompaniesActivity.this, R.drawable.updownempty));
                ivsector.setImageDrawable(ContextCompat.getDrawable(
                        MowaziCompaniesActivity.this, R.drawable.updownempty));

                MyApplication.editor.putString("button", "ivmodification");
                if (ivmodification.getTag().equals("desc")) {
                    ivmodification.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updown));
                    ivmodification.setRotation(180);

                    ivmodification.setTag("asce");
                    sortModification("asce");
                    MyApplication.editor.putString("CompaniesSortKind", "modification");
                    MyApplication.editor.putString("CompaniesSortType", "asce");
                    MyApplication.editor.apply();
                } else {
                    ivmodification.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updown));
                    ivmodification.setRotation(ivmodification.getRotation() - 180);
                    ivmodification.setTag("desc");
                    sortModification("desc");
                    MyApplication.editor.putString("CompaniesSortKind", "modification");
                    MyApplication.editor.putString("CompaniesSortType", "desc");
                    MyApplication.editor.apply();
                }

            }
        });
        setImageForSorting();
    }

    public void setImageForSorting() {
        if (!MyApplication.mshared.getString("CompaniesSortKind", "").equals("")) {
            if (!MyApplication.mshared.getString("button", "").equals("")) {
                if (MyApplication.mshared.getString("button", "").equals("ivsymbol")) {
                    ivmodification.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updownempty));
                    ivsector.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updownempty));
                    if (MyApplication.mshared.getString("CompaniesSortType", "").equals(
                            "asce")) {

                        ivsymbol.setImageDrawable(ContextCompat.getDrawable(
                                MowaziCompaniesActivity.this, R.drawable.updown));

                        ivsymbol.setRotation(180);
                    } else {
                        ivsymbol.setImageDrawable(ContextCompat.getDrawable(
                                MowaziCompaniesActivity.this, R.drawable.updown));
                    }
                } else if (MyApplication.mshared.getString("button", "").equals(
                        "ivmodification")) {

                    ivsymbol.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updownempty));
                    ivsector.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updownempty));

                    if (MyApplication.mshared.getString("CompaniesSortType", "").equals(
                            "asce")) {

                        ivmodification.setImageDrawable(ContextCompat
                                .getDrawable(MowaziCompaniesActivity.this,
                                        R.drawable.updown));

                        ivmodification.setRotation(180);
                    } else {
                        ivmodification.setImageDrawable(ContextCompat
                                .getDrawable(MowaziCompaniesActivity.this,
                                        R.drawable.updown));
                    }
                } else if (MyApplication.mshared.getString("button", "").equals("ivsector")) {
                    ivmodification.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updownempty));
                    ivsymbol.setImageDrawable(ContextCompat.getDrawable(
                            MowaziCompaniesActivity.this, R.drawable.updownempty));

                    if (MyApplication.mshared.getString("CompaniesSortType", "").equals(
                            "asce")) {

                        ivsector.setImageDrawable(ContextCompat.getDrawable(
                                MowaziCompaniesActivity.this, R.drawable.updown));

                        ivsector.setRotation(180);
                    } else {
                        ivsector.setImageDrawable(ContextCompat.getDrawable(
                                MowaziCompaniesActivity.this, R.drawable.updown));
                    }
                }
            }
        }
    }

    public void sort(final String type) {
        companiestoSort.addAll(companies);
        companies.clear();
        Collections.sort(companiestoSort, new Comparator<MowaziCompany>() {

            public int compare(MowaziCompany item1, MowaziCompany item2) {

                if (type.equals("asce")) {
                    if (MyApplication.lang == MyApplication.ENGLISH) {
                        if (item2.getSymbolEn() != null)
                            return item1.getSymbolEn().compareTo(
                                    item2.getSymbolEn());
                        else
                            return 1;
                    } else {
                        if (item2.getSymbolAr() != null)
                            return item1.getSymbolAr().compareTo(
                                    item2.getSymbolAr());
                        else
                            return 1;
                    }
                } else {

                    if (MyApplication.lang == MyApplication.ENGLISH) {
                        if (item2.getSymbolEn() != null)
                            return item2.getSymbolEn().compareTo(
                                    item1.getSymbolEn());
                        else
                            return 1;
                    } else {
                        if (item2.getSymbolAr() != null)
                            return item2.getSymbolAr().compareTo(
                                    item1.getSymbolAr());
                        else
                            return 1;
                    }
                }
            }

        });
        companies.addAll(companiestoSort);
        adapter.notifyDataSetChanged();
        companiestoSort.clear();

    }

    public void sortSector(final String type) {
        companiestoSort.addAll(companies);
        companies.clear();
        Collections.sort(companiestoSort, new Comparator<MowaziCompany>() {

            public int compare(MowaziCompany item1, MowaziCompany item2) {

                if (type.equals("asce")) {
                    if (item2.getSector() != null && item1.getSector() != null)
                        return item1.getSector().getName()
                                .compareTo(item2.getSector().getName());
                    else
                        return 1;
                } else {
                    if (item2.getSector() != null && item1.getSector() != null)
                        return item2.getSector().getName()
                                .compareTo(item1.getSector().getName());
                    else
                        return 1;

                }
            }

        });
        companies.addAll(companiestoSort);
        adapter.notifyDataSetChanged();
        companiestoSort.clear();
    }

    public void sortModification(final String type) {
        companiestoSort.addAll(companies);
        companies.clear();
        Collections.sort(companiestoSort, new Comparator<MowaziCompany>() {

            public int compare(MowaziCompany item1, MowaziCompany item2) {
                if (item1.getLastUpdate() != null
                        && item2.getLastUpdate() != null) {
                    String date1 = item1.getLastUpdate().substring(0,
                            item1.getLastUpdate().indexOf("T"));
                    String date2 = item2.getLastUpdate().substring(0,
                            item2.getLastUpdate().indexOf("T"));
                    if (type.equals("asce")) {
                        if (date2 != null)
                            return date1.compareTo(date2);
                        else
                            return 1;
                    } else {
                        if (date2 != null)
                            return date2.compareTo(date1);
                        else
                            return 1;

                    }

                } else
                    return 1;
            }
        });
        companies.addAll(companiestoSort);
        adapter.notifyDataSetChanged();
        companiestoSort.clear();
    }

    public void clear(View v) {
        sectorId = 0;
        spinnerSectors.setSelection(0);
        spinnerCompanies.setText("");
        companies.clear();
        // companies.add(new Company());
        refreshItems();
    }

    private void refreshItems() {

        // GetCompanesAsycnk get = new GetCompanesAsycnk();
        // get.execute(getString(R.string.url) +
        // getString(R.string.companyservice) + "countryId=" +
        // getString(R.string.countryId) + "&top=50&id=0");

        swipeContainer.setRefreshing(false);

        GetCompaniesBySector get = new GetCompaniesBySector();
        get.execute("0");
    }

    private void onItemsLoadComplete() {
        Log.d("Recycler", "Refreshed");
        swipeContainer.setRefreshing(false);
    }

    public void search(View v) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(
                getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        companies.clear();
        // companies.add(new Company());
        sectorId = selectedsector.getId();
        getCompaniesBySector = new GetCompaniesBySector();
        getCompaniesBySector.execute("0");
    }

    public void closeFilter(View v) {
        if (searchlayoutall.getVisibility() == View.VISIBLE) {
            searchlayoutall.setVisibility(View.GONE);
            sectorId = 0;
            spinnerCompanies.setText("");
        } else {
            searchlayoutall.setVisibility(View.VISIBLE);

            sectorId = selectedsector.getId();
        }

    }

    public void additems() {
        if (companies.size() > 0) {

            // GetCompanesAsycnk get = new GetCompanesAsycnk();
            // get.execute(getString(R.string.url) +
            // getString(R.string.companyservice) + "countryId=" +
            // getString(R.string.countryId) + "&top=20&id=" +
            // companies.get(companies.size() - 1).getCompanyId());

            GetCompaniesBySector get = new GetCompaniesBySector();
            get.execute(LastCompanyId + "");
        }
    }

    public void back(View v) {
        finish();
    }

    public void onItemClicked(View v, int position) {

        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putParcelable("company", companies.get(position));
        i.putExtra("companyid", companies.get(position).getCompanyId() + "");
        i.putExtra("forbid", companies.get(position).getForBid() + "");
        i.setClass(MowaziCompaniesActivity.this, MowaziCompanyDetailsActivity.class);
        MowaziCompaniesActivity.this.startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        sectorId = 0;
        spinnerSectors.setSelection(0);
        spinnerCompanies.setText("");
        companies.clear();

        get = new GetCompaniesBySector();
        get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0");

    }

    protected class GetCompaniesBySector extends
            AsyncTask<String, Void, String> {

        String companytext = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // if(!pulltoRefresh)

            progress.setVisibility(View.VISIBLE);
            companytext = spinnerCompanies.getText().toString();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetCompaniesBySector?";

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("sectorId", "" + sectorId);
            parameters.put("countryId", "116");
            parameters.put("text", companytext);
            parameters.put("top", "40");
            parameters.put("forbid", "-1");
            parameters.put("lang", MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");

            parameters.put("id", params[0]);

            result = ConnectionRequests.POST(url, parameters);

            MowaziCompanyParser parser = new MowaziCompanyParser(result, MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
            try {
                if (pulltoRefresh) {
                    companies.clear();
                    // companies.add(new Company());
                    pulltoRefresh = false;
                }

                companies.addAll(parser.GetCompanies());
                if (companies.size() != 0)
                    LastCompanyId = companies.get(companies.size() - 1)
                            .getCompanyId();

                Log.wtf("LastCompanyId", "" + LastCompanyId);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);

            adapter.notifyDataSetChanged();
            if (!MyApplication.mshared.getString("CompaniesSortKind", "").equals("")) {
                if (MyApplication.mshared.getString("CompaniesSortKind", "").equals(
                        "modification"))
                    sortModification(MyApplication.mshared.getString("CompaniesSortType",
                            "asce"));
                else if (MyApplication.mshared.getString("CompaniesSortKind", "").equals(
                        "symbol"))
                    sort(MyApplication.mshared.getString("CompaniesSortType", "asce"));
                else if (MyApplication.mshared.getString("CompaniesSortKind", "").equals(
                        "sector"))
                    sortSector(MyApplication.mshared.getString("CompaniesSortType", "asce"));
            }
            onItemsLoadComplete();

            if (companies.size() == 0) {
                tvNoData.setVisibility(View.VISIBLE);
                swipeContainer.setVisibility(View.GONE);
            } else {
                tvNoData.setVisibility(View.GONE);
                swipeContainer.setVisibility(View.VISIBLE);
            }
            progress.setVisibility(View.GONE);

            flagLoading = false;
            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    protected class GetSectors extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetSectors?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("lang", MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");

            result = ConnectionRequests.POST(url, parameters);

            MowaziSectorParser parser = new MowaziSectorParser(result, MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
            try {
                allSectors.clear();
                MowaziSector sector = new MowaziSector();
                sector.setId(0);
                sector.setName("-- " + getResources().getString(R.string.sector) + " --");
                allSectors.add(sector);
                allSectors.addAll(parser.GetSectors());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);

            sectorSpinnerAdapter.notifyDataSetChanged();
        }
    }
}
