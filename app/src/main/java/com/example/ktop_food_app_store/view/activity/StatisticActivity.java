package com.example.ktop_food_app_store.view.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.model.data.entity.TopSellingItem;
import com.example.ktop_food_app_store.view.adapter.TopSellingAdapter;
import com.example.ktop_food_app_store.viewmodel.StatisticViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatisticActivity extends AppCompatActivity {
    private TextView selectedDateText, monthlyRevenueText, dailyRevenueText, totalOrdersText, completedOrdersText,
            cancelledOrdersText, totalCustomersText, statisticTitle;
    private ImageView previousDayButton, nextDayButton, backButton;
    private RecyclerView topSellingItemsRecyclerView;
    private TopSellingAdapter topSellingAdapter;
    private List<TopSellingItem> topSellingItems;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private DecimalFormat decimalFormat;
    private StatisticViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        // Khởi tạo các view với id mới
        statisticTitle = findViewById(R.id.statistic_title);
        selectedDateText = findViewById(R.id.selected_date_text);
        monthlyRevenueText = findViewById(R.id.monthly_revenue_text);
        dailyRevenueText = findViewById(R.id.daily_revenue_text);
        totalOrdersText = findViewById(R.id.total_orders_text);
        completedOrdersText = findViewById(R.id.completed_orders_text);
        cancelledOrdersText = findViewById(R.id.cancelled_orders_text);
        totalCustomersText = findViewById(R.id.total_customers_text);
        topSellingItemsRecyclerView = findViewById(R.id.top_selling_items_recycler_view);
        previousDayButton = findViewById(R.id.previous_day_button);
        nextDayButton = findViewById(R.id.next_day_button);
        backButton = findViewById(R.id.back_button);

        // Khởi tạo RecyclerView
        topSellingItems = new ArrayList<>();
        topSellingAdapter = new TopSellingAdapter(this, topSellingItems);
        topSellingItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        topSellingItemsRecyclerView.setAdapter(topSellingAdapter);

        // Khởi tạo thời gian (lấy ngày hiện tại động từ hệ thống)
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        updateDateText();

        // Khởi tạo DecimalFormat để định dạng tiền
        decimalFormat = new DecimalFormat("#,###");
        decimalFormat.setGroupingSize(3);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(StatisticViewModel.class);

        // Quan sát LiveData để cập nhật giao diện
        viewModel.getMonthlyRevenue().observe(this, monthlyRevenue -> {
            monthlyRevenueText.setText(formatCurrency(monthlyRevenue));
        });

        viewModel.getDailyRevenue().observe(this, dailyRevenue -> {
            dailyRevenueText.setText(formatCurrency(dailyRevenue));
        });

        viewModel.getTotalOrders().observe(this, totalOrders -> {
            totalOrdersText.setText(String.valueOf(totalOrders));
        });

        viewModel.getCompletedOrders().observe(this, completedOrders -> {
            completedOrdersText.setText(String.valueOf(completedOrders));
        });

        viewModel.getCancelledOrders().observe(this, cancelledOrders -> {
            cancelledOrdersText.setText(String.valueOf(cancelledOrders));
        });

        viewModel.getTotalCustomers().observe(this, totalCustomers -> {
            totalCustomersText.setText(String.valueOf(totalCustomers));
        });

        viewModel.getTopSellingItems().observe(this, topItems -> {
            topSellingItems.clear();
            topSellingItems.addAll(topItems);
            topSellingAdapter.notifyDataSetChanged();
        });

        // Xử lý sự kiện nút điều hướng (chuyển đổi ngày)
        previousDayButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            updateDateText();
            loadStatistics();
        });
        nextDayButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            updateDateText();
            loadStatistics();
        });
        // Xử lý sự kiện nhấn vào selected_date_text để hiển thị DatePickerDialog
        selectedDateText.setOnClickListener(v -> showDatePickerDialog());
        backButton.setOnClickListener(v -> finish());
        // Tải dữ liệu thống kê
        loadStatistics();
    }

    private void updateDateText() {
        selectedDateText.setText(dateFormat.format(calendar.getTime()));
    }

    // Hiển thị DatePickerDialog
    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Cập nhật calendar với ngày được chọn
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                    updateDateText();
                    loadStatistics();
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // Phương thức định dạng tiền
    private String formatCurrency(double amount) {
        return decimalFormat.format(amount) + " đ";
    }

    private void loadStatistics() {
        int selectedYear = calendar.get(Calendar.YEAR);
        int selectedMonth = calendar.get(Calendar.MONTH);
        int selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        viewModel.loadStatistics(selectedYear, selectedMonth, selectedDay);
    }
}