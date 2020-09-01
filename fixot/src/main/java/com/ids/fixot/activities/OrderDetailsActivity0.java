package com.ids.fixot.activities;

import android.support.v7.app.AppCompatActivity;


public class OrderDetailsActivity0 extends AppCompatActivity {
/*
//    OrderDurationType orderDurationType = new OrderDurationType();
//    Calendar myCalendar;
//    DatePickerDialog.OnDateSetListener date;
//    RelativeLayout rlUserHeader, rlLayout;
//    TextView tvUserName, tvPortfolioNumber, tvStockTitle;
//    RecyclerView rvOrderDetails;
//    GridLayoutManager llm;
//    StockQuotation stockQuotation;
//    private boolean started = false;
//    ValuesListArrayAdapter adapter;
//    Button btCancel, btEdit, btQuickEdit ;
//    ImageView ivPortfolio;
//    private ArrayList<ValueItem> allValueItems = new ArrayList<>();
//    OnlineOrder onlineOrder;
//
//    FloatingActionMenu famOrderMenu, famOrderMenuRTL;
//    FloatingActionButton fabFastEdit, fabEdit, fabCancel;
//    FloatingActionButton fabFastEditRTL, fabEditRTL, fabCancelRTL;
//    Double orderPrice;
//    Boolean initially = true;
//    int orderQuantity, orderType;
//    String orderGoodUntilDate = "";
//    String dateFormatter = "dd/MM/yyyy HH:mm:ss";
//
//    OrderDurationType previous;
//    PatchedSpinner spDurationType;
//    Boolean openCalendar = true;
//
//    int spinerLastPosition = 0 , spinerNewPosition = 0;
//
//
//    public OrderDetailsActivity() {
//        LocalUtils.updateConfig(this);
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Actions.setLocal(MyApplication.lang, this);
//        setContentView(R.layout.activity_order_details);
//
//        Actions.initializeBugsTracking(this);
//        Actions.initializeToolBar(getString(R.string.order_details), OrderDetailsActivity.this);
        Actions.showHideFooter(this);
//
//        started = true;
//
//        findViews();
//
//        if (getIntent().hasExtra("order")) {
//
//            onlineOrder = getIntent().getExtras().getParcelable("order");
//            stockQuotation = Actions.getStockQuotationById(MyApplication.stockQuotations, Integer.parseInt(onlineOrder.getStockID()));
//            Log.wtf("onlineOrder Good Until Date","Date = " + onlineOrder.getGoodUntilDate() );
//            Log.wtf("onlineOrder Good Until Date","Date = " + onlineOrder.getDurationID() );
//            allValueItems.addAll(onlineOrder.getAllvalueItems());
//
//            adapter.notifyDataSetChanged();
//        } else {
//            onlineOrder = new OnlineOrder();
//            stockQuotation = new StockQuotation();
//        }
//
//        setOrderOptions();
//
//        //String stockTitle = stockQuotation.getStockID() + "-" + (MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getSymbolEn());
//        String stockTitle = onlineOrder.getStockID() + "-" + onlineOrder.getStockSymbol();
//        tvStockTitle.setText(stockTitle);
//
//        Actions.overrideFonts(this, rlLayout, false);
//
//        if (MyApplication.lang == MyApplication.ARABIC){
//
//            tvUserName.setText(MyApplication.currentUser.getNameAr());
//            tvUserName.setTypeface(MyApplication.droidbold);
//            tvStockTitle.setTypeface(MyApplication.droidbold);
//            tvPortfolioNumber.setTypeface(MyApplication.droidbold);
//        }else{
//
//            tvUserName.setText(MyApplication.currentUser.getNameEn());
//            tvUserName.setTypeface(MyApplication.giloryBold);
//            tvStockTitle.setTypeface(MyApplication.giloryBold);
//            tvPortfolioNumber.setTypeface(MyApplication.giloryBold);
//        }
//    }
//
//    private void setOrderOptionsMenu(){
//
////        famOrderMenu.setVisibility(MyApplication.lang == MyApplication.ENGLISH ? View.VISIBLE : View.GONE);
////        famOrderMenuRTL.setVisibility(MyApplication.lang == MyApplication.ARABIC ? View.VISIBLE : View.GONE);
//
//        famOrderMenu.setVisibility(View.GONE);
//        famOrderMenuRTL.setVisibility(View.GONE);
//    }
//
//    private void setOrderOptions(){
//
//        orderType = onlineOrder.getOrderTypeID();
//
//        fabCancel.setEnabled(onlineOrder.isCanDelete());
//        fabEdit.setEnabled(onlineOrder.isCanUpdate());
//        fabFastEdit.setEnabled(onlineOrder.isCanUpdate());
//
//        fabCancelRTL.setEnabled(onlineOrder.isCanDelete());
//        fabEditRTL.setEnabled(onlineOrder.isCanUpdate());
//        fabFastEditRTL.setEnabled(onlineOrder.isCanUpdate());
//
////        btCancel.setEnabled(onlineOrder.isCanDelete());
////        btEdit.setEnabled(onlineOrder.isCanUpdate());
////        btQuickEdit.setEnabled(onlineOrder.isCanUpdate());
//
//        btCancel.setBackgroundColor(onlineOrder.isCanDelete() ? getResources().getColor(R.color.colorDark) : getResources().getColor(R.color.lightgrey));
//        btEdit.setBackgroundColor(onlineOrder.isCanUpdate() ? getResources().getColor(R.color.colorDark) : getResources().getColor(R.color.lightgrey));
//        btQuickEdit.setBackgroundColor(onlineOrder.isCanUpdate() ? getResources().getColor(R.color.colorDark) : getResources().getColor(R.color.lightgrey));
//    }
//
//    public void back(View v) {
//
//        finish();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        Actions.InitializeSessionService(this);
//        Actions.InitializeMarketService(this);
//        Actions.checkLanguage(this, started);
//    }
//
//    private void findViews() {
//
//        famOrderMenu = findViewById(R.id.famOrderMenu);
//        fabFastEdit = findViewById(R.id.fabFastEdit);
//        fabEdit = findViewById(R.id.fabEdit);
//        fabCancel = findViewById(R.id.fabCancel);
//
//        famOrderMenuRTL = findViewById(R.id.famOrderMenuRTL);
//        fabFastEditRTL = findViewById(R.id.fabFastEditRTL);
//        fabEditRTL = findViewById(R.id.fabEditRTL);
//        fabCancelRTL = findViewById(R.id.fabCancelRTL);
//
//        rlUserHeader = findViewById(R.id.rlUserHeader);
//        tvUserName = rlUserHeader.findViewById(R.id.tvUserName);
//        tvPortfolioNumber = rlUserHeader.findViewById(R.id.tvPortfolioNumber);
//        ivPortfolio = rlUserHeader.findViewById(R.id.ivPortfolio);
//        rlLayout = findViewById(R.id.rlLayout);
//        rvOrderDetails = findViewById(R.id.rvOrderDetails);
//        tvStockTitle = findViewById(R.id.tvStockTitle);
//        btEdit = findViewById(R.id.btEdit);
//        btQuickEdit = findViewById(R.id.btQuickEdit);
//        btCancel = findViewById(R.id.btCancel);
//        llm = new GridLayoutManager(this, MyApplication.VALUES_SPAN_COUNT);
//        rvOrderDetails.setLayoutManager(llm);
//        adapter = new ValuesListArrayAdapter(this, allValueItems);
//        rvOrderDetails.setAdapter(adapter);
//
//
//        setOrderOptionsMenu();
//    }
//
//    private void updateLabel(EditText editText) {
//        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatter, Locale.ENGLISH);
//        editText.setText(sdf.format(myCalendar.getTime()));
//    }
//
//    public void goTo(View v) {
//
//        switch (v.getId()) {
//
//            case R.id.btTimeSales:
//                startActivity(new Intent(OrderDetailsActivity.this, TimeSalesActivity.class)
//                        .putExtra("stockId", Integer.parseInt(onlineOrder.getStockID()))
//                        .putExtra("stockName", onlineOrder.getStockName())//MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn())
//                );
//                break;
//
//            case R.id.btOrderBook:
//                startActivity(new Intent(OrderDetailsActivity.this, StockOrderBookActivity.class)
//                        .putExtra("stockId", Integer.parseInt(onlineOrder.getStockID()))
//                        .putExtra("stockName", onlineOrder.getStockName())//MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn())
//                );
//                break;
//
//            case R.id.fabFastEdit:
//                if (MyApplication.allOrderDurationType.size() > 0){
//
//                    showFastEditDialog(MyApplication.allOrderDurationType);
//                }else{
//
//                    new GetOrderDurationTypes().execute();
//                }
//                break;
//
//            case R.id.fabFastEditRTL:
//
//                if (MyApplication.allOrderDurationType.size() > 0){
//
//                    showFastEditDialog(MyApplication.allOrderDurationType);
//                }else{
//
//                    new GetOrderDurationTypes().execute();
//                }
//
//                break;
//
//            case R.id.btQuickEdit:
//
//                if (MyApplication.allOrderDurationType.size() > 0){
//
//                    showFastEditDialog(MyApplication.allOrderDurationType);
//                }else{
//
//                    new GetOrderDurationTypes().execute();
//                }
//
//                break;
//
//            case R.id.fabEdit:
//
//                try {
//                    Bundle b = new Bundle();
//                    b.putInt("action", onlineOrder.getTradeTypeID());
//                    b.putBoolean("isFromOrderDetails", true);
//                    b.putParcelable("stockQuotation", stockQuotation);
//                    b.putParcelable("onlineOrder", onlineOrder);
//                    Intent i = new Intent(OrderDetailsActivity.this, TradesActivity.class);
//                    i.putExtras(b);
//
//                    OrderDetailsActivity.this.startActivity(i);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//
//            case R.id.fabEditRTL:
//
//                try {
//                    Bundle b = new Bundle();
//                    b.putInt("action", onlineOrder.getTradeTypeID());
//                    b.putBoolean("isFromOrderDetails", true);
//                    b.putParcelable("stockQuotation", stockQuotation);
//                    b.putParcelable("onlineOrder", onlineOrder);
//                    Intent i = new Intent(OrderDetailsActivity.this, TradesActivity.class);
//                    i.putExtras(b);
//
//                    OrderDetailsActivity.this.startActivity(i);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//
//            case R.id.btEdit:
//                try {
//
//                    Bundle b = new Bundle();
//                    b.putInt("action", onlineOrder.getTradeTypeID());
//                    b.putBoolean("isFromOrderDetails", true);
//                    b.putParcelable("stockQuotation", stockQuotation);
//                    b.putParcelable("onlineOrder", onlineOrder);
//                    Intent i = new Intent(OrderDetailsActivity.this, TradesActivity.class);
//                    i.putExtras(b);
//
//                    OrderDetailsActivity.this.startActivity(i);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//
//            case R.id.btCancel:
//                showCancelDialog();
//                break;
//
//            case R.id.fabCancel:
//                showCancelDialog();
//                break;
//
//            case R.id.fabCancelRTL:
//                showCancelDialog();
//                break;
//        }
//    }
//
//    private void setLimitChecked(Button btLimit, Button btMarketPrice, boolean limitChecked) {
//
//        Log.wtf("limit checked", "is "+ limitChecked);
//
//        if (limitChecked) {
//
//            btLimit.setTextColor(ContextCompat.getColor(this, R.color.white));
//            btLimit.setBackground(ContextCompat.getDrawable(this, R.drawable.border_limit_selected));
//
//            btMarketPrice.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
//            btMarketPrice.setBackground(ContextCompat.getDrawable(this, R.drawable.border_market_not_selected));
//        } else {
//
//            btMarketPrice.setTextColor(ContextCompat.getColor(this, R.color.white));
//            btMarketPrice.setBackground(ContextCompat.getDrawable(this, R.drawable.border_market_selected));
//
//            btLimit.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
//            btLimit.setBackground(ContextCompat.getDrawable(this, R.drawable.border_limit_not_selected));
//        }
//    }
//
//    private void showLimitPrice(LinearLayout llPrice, boolean show) {
//
//        /*etLimitPrice.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//        btLimitMinus.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//        btLimitPlus.setVisibility(show ? View.VISIBLE : View.INVISIBLE);*/
//
//        Log.wtf("show","sho : " + show);
//
//        llPrice.setVisibility(show ? View.VISIBLE : View.GONE);
//
//    }
//
//    private void showFastEditDialog(ArrayList<OrderDurationType> allOrderDurations){
//
//        //ContextThemeWrapper ctw = new ContextThemeWrapper( this, R.style.AlertDialogCustom);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        String goodUntil = "";
//
//        LinearLayout llPrice;
//        OrderDurationTypeAdapter adapter;
//        EditText etConfirm, etLimitPrice, etQuantity, etDurationType;
//        Button btLimit, btMarketPrice;
//        Button btLimitPlus, btLimitMinus;
//        Button btQuantityPlus, btQuantityMinus;
//        ImageView ivArrow;
//
//        final View editDialog = inflater.inflate(R.layout.popup_edit_dialog, null);
//
//        llPrice = editDialog.findViewById(R.id.llPrice);
//
//        spDurationType = editDialog.findViewById(R.id.spDurationType);
//        ivArrow = editDialog.findViewById(R.id.ivArrow);
//
//        etConfirm = editDialog.findViewById(R.id.etConfirm);
//        etLimitPrice = editDialog.findViewById(R.id.etLimitPrice);
//        etQuantity = editDialog.findViewById(R.id.etQuantity);
//        etDurationType = editDialog.findViewById(R.id.etDurationType);
//
//        btLimit = editDialog.findViewById(R.id.btOrderLimit);
//        btMarketPrice = editDialog.findViewById(R.id.btOrderMarketPrice);
//
//        btLimitPlus = editDialog.findViewById(R.id.btLimitPlus);
//        btLimitMinus = editDialog.findViewById(R.id.btLimitMinus);
//
//        btQuantityPlus = editDialog.findViewById(R.id.btQuantityPlus);
//        btQuantityMinus = editDialog.findViewById(R.id.btQuantityMinus);
//
//        Log.wtf("onlineOrder.getGoodUntilDate()", "onlineOrder.getGoodUntilDate() = " + onlineOrder.getGoodUntilDate());
//
//      //  if (initially) {
//        goodUntil = onlineOrder.getGoodUntilDate();
//            try {
//                SimpleDateFormat sdf = new SimpleDateFormat(dateFormatter, Locale.ENGLISH);
//                Date date = sdf.parse(goodUntil);
//                goodUntil = sdf.format(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            etDurationType.setText(goodUntil);
//      //  }
//        Log.wtf("etDurationType", "value = " + etDurationType.getText());
//
//        if (orderType == MyApplication.LIMIT){
//
//            btLimit.performClick();
//            setLimitChecked(btLimit, btMarketPrice, true);
//            showLimitPrice(llPrice, true);
//        }else {
//
//            btMarketPrice.performClick();
//            setLimitChecked(btLimit, btMarketPrice, false);
//            showLimitPrice(llPrice, false);
//        }
//
//        btLimit.setOnClickListener(v -> {
//            Log.wtf("order", "limit");
//            orderType = MyApplication.LIMIT;
//            setLimitChecked(btLimit, btMarketPrice, true);
//            showLimitPrice(llPrice, true);
//        });
//
//        btMarketPrice.setOnClickListener(v -> {
//            Log.wtf("order", "market");
//            orderType = MyApplication.MARKET_PRICE;
//            setLimitChecked(btLimit, btMarketPrice, false);
//            showLimitPrice(llPrice, false);
//        });
//
//        ivArrow.setOnClickListener(v -> spDurationType.performClick());
//
//        adapter = new OrderDurationTypeAdapter(this, allOrderDurations);
//        spDurationType.setAdapter(adapter);
//        spinerLastPosition = Actions.returnDurationIndex(onlineOrder.getDurationID());
//        Log.wtf("spDurationType.setSelection","index onlineOrder.getDurationID() : " + Actions.returnDurationIndex(onlineOrder.getDurationID()) );
//        spDurationType.setSelection(Actions.returnDurationIndex(onlineOrder.getDurationID()));
//
//        orderDurationType = adapter.getItem(spDurationType.getSelectedItemPosition());
//
//        previous = orderDurationType;
//
//
//        spDurationType.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                spinerNewPosition = position;
//
//                Log.wtf("initially",""+initially);
//                Log.wtf("goodUntil in shwing order"," goodUntil : " + onlineOrder.getGoodUntilDate());
//                //<editor-fold desc="position">
//
//                orderDurationType = adapter.getItem(position);
//                Log.wtf("previous id",""+previous.getDescriptionEn() + " - position : " + position);
//                Log.wtf("orderDurationType index",":: " + Actions.returnDurationIndex(orderDurationType.getID()) );
//
//                //     previous = current;
//                //     current = (OrderDurationType) parent.getItemAtPosition(position);
//
//                if (orderDurationType.getID() == 6) {
//
//                    etDurationType.setVisibility(View.VISIBLE);
//                    //    etDurationType.setText("");    //Mkobaissy
//
////                        if (initially) {
////
//                            String goodUntil = onlineOrder.getGoodUntilDate();
//
//                    Log.wtf("goodUntil",""+onlineOrder.getGoodUntilDate());
//
//                            try {
//                                SimpleDateFormat sdf = new SimpleDateFormat(dateFormatter, Locale.ENGLISH);
//                                Date date = sdf.parse(goodUntil);
//                                goodUntil = sdf.format(date);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//
//                            etDurationType.setText(goodUntil);
////                            initially = false;
////                        } else {
//                            showDateDialog();
//                      //  }
//
//                } else {
//                    spinerLastPosition = spinerNewPosition;
//                    previous = orderDurationType;
//                    etDurationType.setVisibility(View.GONE);
//                }
//                //</editor-fold>
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//        date = (view, year, monthOfYear, dayOfMonth) -> {
//            if (year >= Calendar.getInstance().get(Calendar.YEAR)) {
//                // TODO Auto-generated method stub
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                Log.wtf("date","value change");
//                spinerLastPosition = spinerNewPosition;
//                updateLabel(etDurationType);
//
//                orderGoodUntilDate = etDurationType.getText().toString();
//            }
//        };
//
//        orderQuantity = onlineOrder.getQuantity() - onlineOrder.getQuantityExecuted();
//        etQuantity.setText(String.valueOf(orderQuantity));
//
//        orderPrice = onlineOrder.getPrice();
//        etLimitPrice.setText(String.valueOf(orderPrice));
//
//        btLimitPlus.setOnClickListener(v -> {
//            orderPrice += 1;
//            etLimitPrice.setText(Actions.formatNumber(orderPrice, "##.##"));
//        });
//
//        btLimitMinus.setOnClickListener(v -> {
//
//            if (orderPrice > 0) {
//                orderPrice -= 1;
//            }
//            etLimitPrice.setText(Actions.formatNumber(orderPrice, "##.##"));
//        });
//
//        btQuantityPlus.setOnClickListener(v -> {
//
//            try{
//
//                if (etQuantity.getText().toString().length() > 0){
//
//                    orderQuantity = Integer.parseInt(etQuantity.getText().toString());
//                }else{
//                    orderQuantity = 0;
//                }
//
//                orderQuantity += 1;
//                etQuantity.setText(String.valueOf(orderQuantity));
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        });
//
//        btQuantityMinus.setOnClickListener(v -> {
//
//            try{
//
//                if (etQuantity.getText().toString().length() > 0){
//
//                    orderQuantity = Integer.parseInt(etQuantity.getText().toString());
//                    orderQuantity -= 1;
//                }else{
//                    orderQuantity = 0;
//                }
//                etQuantity.setText(String.valueOf(orderQuantity));
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        });
//
//        builder.setView(editDialog)
//        .setPositiveButton(getResources().getString(R.string.send_button), (dialog, which) -> new UpdateOrder(onlineOrder, etConfirm.getText().toString(), orderQuantity, orderType, orderPrice, orderGoodUntilDate).execute())
//        .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
//
//        final AlertDialog dialog = builder.create();
//        dialog.setOnShowListener(arg0 -> {
//            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(OrderDetailsActivity.this, R.color.colorDark));
//            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(OrderDetailsActivity.this, R.color.colorDark));
//            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTransformationMethod(null);
//            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
//        });
//        dialog.show();
//    }
//
//    private void showDateDialog() {
//
//        Log.wtf("showDateDialog" , "openCalendar = " + openCalendar);
//        myCalendar = Calendar.getInstance();
//        myCalendar.roll(Calendar.DATE, 1);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(OrderDetailsActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
//
//
//        Log.wtf("spinerLastPosition = " + spinerLastPosition," spinerNewPosition = " + spinerNewPosition);
//
//        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == DialogInterface.BUTTON_NEGATIVE) {
//                    // Do Stuff
//                    int index = Actions.returnDurationIndex(previous.getID());
//                    Log.wtf("isFromOrderDetails","spinerLastPosition index : " + spinerLastPosition);
//
//                    if(spinerLastPosition != spinerNewPosition){
//                        openCalendar = false ;
//                        spDurationType.setSelection(spinerLastPosition);
//                    }
//                }
//            }
//        });
//
////        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.update_ok), new DialogInterface.OnClickListener() {
////            public void onClick(DialogInterface dialog, int which) {
////                if (which == DialogInterface.BUTTON_POSITIVE) {
////                    // Do Stuff
////                    spinerLastPosition = spinerNewPosition;
////                }
////            }
////        });
//
//        //
//
//     //   if(openCalendar) {
//            datePickerDialog.show();
////        }else{
////            openCalendar = true;
////        }
//    }
//
//
//
//    private void showCancelDialog(){
//
//        new AlertDialog.Builder(OrderDetailsActivity.this)
//                .setTitle(getResources().getString(R.string.cancel_order))
//                .setMessage(getResources().getString(R.string.cancel_order_text))
//                .setPositiveButton(android.R.string.yes,
//                        (dialog, which) -> new CancelOrder().execute())
//                .setNegativeButton(android.R.string.no,
//                        (dialog, which) -> {
//                            // do nothing
//                        }).setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
//    }
//
//    private class CancelOrder extends AsyncTask<Void, Void, String> {
//
//        String random = Actions.getRandom();
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            try {
//                MyApplication.showDialog(OrderDetailsActivity.this);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            String result = "";
//            String url = MyApplication.link + "/CancelOrder";
//
//
//            JSONStringer stringer = null;
//            try {
//                stringer = new JSONStringer()
//                        .object()
//                        .key("ApplicationType").value(0)
//                        .key("Reference").value(onlineOrder.getReference())
//                        .key("key").value(MyApplication.currentUser.getKey())
//                        .endObject();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            //String stringer = "{\"ApplicationType\":\"1\",\" Reference\":" + onlineOrder.getID() + ",\"key\":\"" + MyApplication.currentUser.getKey() + "\"}";
//
//            result = ConnectionRequests.POSTWCF(url, stringer);
//            Log.wtf("Result", "is "+result);
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            try {
//                MyApplication.dismiss();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            JSONObject object = null;
//            try {
//                object = new JSONObject(result);
//                String success = object.getString("MessageEn");
//
//                if (success.equals("Success")) {
//
//                    cancelDialog(OrderDetailsActivity.this, getResources().getString(R.string.cancelOrderSuccess), true, false);
//
//                } else {
//
//                    cancelDialog(OrderDetailsActivity.this, getResources().getString(R.string.cancelOrderError), false, false);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void cancelDialog(final Activity c, String message, final boolean finish, boolean cancel) {
//
//        ContextThemeWrapper ctw = new ContextThemeWrapper(c, R.style.AlertDialogCustom);
//
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctw);
//        builder
//                .setMessage(message)
//                .setCancelable(true)
//                .setPositiveButton(c.getString(R.string.confirm), (dialog, id) -> {
//                    dialog.cancel();
//                    if (finish) {
//
//                        finishAffinity();
//                        Intent intent = new Intent(OrderDetailsActivity.this, OrdersActivity.class);
//                        //TradeConfirmationActivity.this.finish();
//                        startActivity(intent);
//                    }
//                });
//        if (cancel)
//            builder.setNegativeButton(c.getString(R.string.confirm), (dialog, id) -> dialog.cancel());
//        android.app.AlertDialog alert = builder.create();
//        alert.show();
//
//    }
//
//    private class GetOrderDurationTypes extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            try {
//                MyApplication.showDialog(OrderDetailsActivity.this);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            String result = "";
//            String url = MyApplication.link + "/GetOrderDurationTypes?"; // this method uses key after login
//
//
//            HashMap<String, String> parameters = new HashMap<String, String>();
//
//            parameters.put("key", MyApplication.currentUser.getKey());
//
//            try {
//                result = ConnectionRequests.GET(url, OrderDetailsActivity.this, parameters);
//
//                MyApplication.allOrderDurationType.addAll(GlobalFunctions.GetOrderDurationList(result));
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            try {
//                MyApplication.dismiss();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            showFastEditDialog(MyApplication.allOrderDurationType);
//        }
//    }
//
//    private class UpdateOrder extends AsyncTask<Void, Void, String> {
//
//        OnlineOrder onlineOrder;
//        int quantity, orderType;
//        double price;
//        String pin;
//        String random = "";
//        String tradingPin = "";
//        String encrypted = "";
//        String goodUntilDate;
//
//        private UpdateOrder(OnlineOrder onlineOrder, String pin, int quantity, int orderType, double price, String goodUntilDate){
//            this.onlineOrder = onlineOrder;
//            this.pin = pin;
//            this.quantity = quantity;
//            this.orderType = orderType;
//            this.price = price;
//            this.goodUntilDate = goodUntilDate;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            try {
//                MyApplication.showDialog(OrderDetailsActivity.this);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            random = Actions.getRandom();
//
//            encrypted =  Actions.MD5(pin);
//
//            encrypted = encrypted+random;
//            tradingPin = Actions.MD5(encrypted);
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            String result = "";
//            String url = MyApplication.link + "/UpdateOrder";
//
//            Log.wtf("UpdateOrder","GoodUntilDate : '" + goodUntilDate + "'");
//
//            JSONStringer stringer = null;
//            try {
//                stringer = new JSONStringer()
//                        .object()
//                        .key("UserID").value(MyApplication.currentUser.getId())
//                        .key("InvestorID").value(MyApplication.currentUser.getInvestorId())
//                        .key("PortfolioID").value(MyApplication.currentUser.getPortfolioId())
//                        .key("TradingPIN").value(tradingPin)
//                        .key("Random").value(Integer.parseInt(random))
//                        .key("ApplicationType").value(7)
//                        .key("Reference").value(onlineOrder.getReference())
//                        .key("BrokerID").value(Integer.parseInt(MyApplication.brokerID))
//                        .key("DurationID").value(orderDurationType.getID())
//
//                        .key("GoodUntilDate").value(goodUntilDate)
//
//                        .key("Price").value(price)
//                        .key("OrderTypeID").value(orderType)
//                        .key("Quantity").value(quantity)
//                        .key("StockID").value(onlineOrder.getStockID())
//                        .key("TradeTypeID").value(onlineOrder.getTradeTypeID())
//
//                        .key("StatusID").value(onlineOrder.getStatusID())
//                        .key("OperationTypeID").value(onlineOrder.getOperationTypeID())
//
//                        .key("BrokerEmployeeID").value(0)
//                        .key("ForwardContractID").value(0)
//                        .key("key").value(MyApplication.currentUser.getKey())
//                        .endObject();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.wtf("OrderDetailsActivity : url ='" + url + "'" , "JSONStringer = '" + stringer +"'");
//            result = ConnectionRequests.POSTWCF(url, stringer);
//            Log.wtf("update result", "is "+result);
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            try {
//                MyApplication.dismiss();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            JSONObject object = null;
//            try {
//                object = new JSONObject(result);
//                String success = object.getString("MessageEn");
//                if (success.equals("Success")) {
//
//                    finishAffinity();
//                    Intent intent = new Intent(OrderDetailsActivity.this, OrdersActivity.class);
//                    //TradeConfirmationActivity.this.finish();
//                    startActivity(intent);
//
//                } else {
//
//                    String error;
//                    error = MyApplication.lang == MyApplication.ENGLISH ?  success : object.getString("MessageAr");
//                    Actions.CreateDialog(OrderDetailsActivity.this, error, false, false);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
