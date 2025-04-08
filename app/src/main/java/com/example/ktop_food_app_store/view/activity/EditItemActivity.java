package com.example.ktop_food_app_store.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.model.data.entity.Food;
import com.example.ktop_food_app_store.viewmodel.EditItemViewModel;

import java.util.ArrayList;
import java.util.List;

public class EditItemActivity extends AppCompatActivity {

    private EditText itemNameEditText, itemPriceEditText, urlImageEditText, descriptionEditText;
    private ImageView backButton, urlImageIcon, timeIcon, categoryIcon, imagePreview;
    private Spinner timeSpinner, categorySpinner;
    private TextView timeErrorTextView, categoryErrorTextView;
    private CheckBox bestFoodCheckbox;
    private Button saveItemButton;
    private EditItemViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(EditItemViewModel.class);

        // Khởi tạo các thành phần giao diện
        initializeViews();
        // Lấy dữ liệu món ăn từ Intent
        Food food = (Food) getIntent().getSerializableExtra("food");
        if (food == null) {
            Toast.makeText(this, "Invalid food item", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        viewModel.setFoodItem(food);

        // Thiết lập Spinner cho thời gian và danh mục
        setupSpinners();
        // Thiết lập các sự kiện lắng nghe
        setupListeners();
        // Điền dữ liệu món ăn vào các trường
        populateFields();
        // Quan sát các thay đổi từ ViewModel
        observeViewModel();
    }

    private void initializeViews() {
        // Ánh xạ các thành phần giao diện từ layout
        backButton = findViewById(R.id.back_button);
        itemNameEditText = findViewById(R.id.item_name_edit_text);
        itemPriceEditText = findViewById(R.id.item_price_edit_text);
        urlImageEditText = findViewById(R.id.url_image_edit_text);
        urlImageIcon = findViewById(R.id.url_image_icon);
        imagePreview = findViewById(R.id.image_preview);
        descriptionEditText = findViewById(R.id.description_edit_text);
        timeSpinner = findViewById(R.id.time_spinner);
        timeIcon = findViewById(R.id.time_icon);
        timeErrorTextView = findViewById(R.id.time_error_text_view);
        categorySpinner = findViewById(R.id.category_spinner);
        categoryIcon = findViewById(R.id.category_icon);
        categoryErrorTextView = findViewById(R.id.category_error_text_view);
        bestFoodCheckbox = findViewById(R.id.best_food_checkbox);
        saveItemButton = findViewById(R.id.save_item_button);
    }

    private void setupSpinners() {
        // Thiết lập Spinner cho thời gian
        List<String> timeOptions = viewModel.getTimeOptions();
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeOptions);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        // Thiết lập Spinner cho danh mục
        List<String> categoryOptions = viewModel.getCategoryOptions();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryOptions);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void setupListeners() {
        // Xử lý sự kiện nhấn nút quay lại
        backButton.setOnClickListener(v -> finish());
        // Xử lý sự kiện nhấn nút lưu
        saveItemButton.setOnClickListener(v -> viewModel.saveFoodItem(
                itemNameEditText.getText().toString().trim(),
                itemPriceEditText.getText().toString().trim(),
                urlImageEditText.getText().toString().trim(),
                descriptionEditText.getText().toString().trim(),
                timeSpinner.getSelectedItem().toString(),
                categorySpinner.getSelectedItem().toString(),
                bestFoodCheckbox.isChecked()
        ));

        // Xử lý sự kiện nhấn biểu tượng URL hình ảnh
        urlImageIcon.setOnClickListener(v -> handleImagePreview());

        // Mở Spinner thời gian khi nhấn biểu tượng
        timeIcon.setOnClickListener(v -> timeSpinner.performClick());
        // Mở Spinner danh mục khi nhấn biểu tượng
        categoryIcon.setOnClickListener(v -> categorySpinner.performClick());

        // Theo dõi thay đổi trên các trường nhập liệu để kiểm tra biểu mẫu
        TextWatcher formWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.validateForm(
                        itemNameEditText.getText().toString().trim(),
                        itemPriceEditText.getText().toString().trim(),
                        urlImageEditText.getText().toString().trim(),
                        timeSpinner.getSelectedItem().toString(),
                        categorySpinner.getSelectedItem().toString()
                );
            }
        };

        itemNameEditText.addTextChangedListener(formWatcher);
        itemPriceEditText.addTextChangedListener(formWatcher);
        urlImageEditText.addTextChangedListener(formWatcher);
        descriptionEditText.addTextChangedListener(formWatcher);

        // Theo dõi lựa chọn trên Spinner thời gian
        timeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                viewModel.validateForm(
                        itemNameEditText.getText().toString().trim(),
                        itemPriceEditText.getText().toString().trim(),
                        urlImageEditText.getText().toString().trim(),
                        timeSpinner.getSelectedItem().toString(),
                        categorySpinner.getSelectedItem().toString()
                );
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Theo dõi lựa chọn trên Spinner danh mục
        categorySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                viewModel.validateForm(
                        itemNameEditText.getText().toString().trim(),
                        itemPriceEditText.getText().toString().trim(),
                        urlImageEditText.getText().toString().trim(),
                        timeSpinner.getSelectedItem().toString(),
                        categorySpinner.getSelectedItem().toString()
                );
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Theo dõi trạng thái checkbox "Best Food"
        bestFoodCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.validateForm(
                itemNameEditText.getText().toString().trim(),
                itemPriceEditText.getText().toString().trim(),
                urlImageEditText.getText().toString().trim(),
                timeSpinner.getSelectedItem().toString(),
                categorySpinner.getSelectedItem().toString()
        ));
    }

    private void populateFields() {
        // Điền dữ liệu món ăn vào các trường giao diện
        Food food = viewModel.getFoodItem();
        itemNameEditText.setText(food.getTitle());
        itemPriceEditText.setText(String.valueOf(food.getPrice()));
        urlImageEditText.setText(food.getImagePath());
        descriptionEditText.setText(food.getDescription());
        bestFoodCheckbox.setChecked(food.isBestFood());

        // Thiết lập giá trị hiện tại cho Spinner thời gian
        int timePosition = viewModel.getTimeOptions().indexOf(food.getTimeValue());
        if (timePosition >= 0) timeSpinner.setSelection(timePosition);

        // Thiết lập giá trị hiện tại cho Spinner danh mục
        int categoryPosition = viewModel.getCategoryOptions().indexOf(viewModel.getCategoryNameFromId(food.getCategoryId()));
        if (categoryPosition >= 0) categorySpinner.setSelection(categoryPosition);

        // Tải hình ảnh xem trước nếu có
        if (food.getImagePath() != null && !food.getImagePath().isEmpty()) {
            Glide.with(this).load(food.getImagePath()).into(imagePreview);
        }
    }

    private void handleImagePreview() {
        // Xử lý xem trước hình ảnh từ URL
        String imageUrl = urlImageEditText.getText().toString().trim();
        if (imageUrl.isEmpty() || !Patterns.WEB_URL.matcher(imageUrl).matches()) {
            urlImageEditText.setError("Invalid URL format");
            imagePreview.setImageResource(R.drawable.logo_single);
            return;
        }

        Glide.with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.logo_single)
                .error(R.drawable.logo_single)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imagePreview);
    }

    private void observeViewModel() {
        // Quan sát trạng thái hợp lệ của biểu mẫu
        viewModel.getFormValidity().observe(this, isValid -> {
            saveItemButton.setEnabled(isValid);
            saveItemButton.setBackgroundResource(isValid ? R.drawable.custom_bg_success : R.drawable.custom_bg_default);
        });

        // Quan sát kết quả lưu món ăn
        viewModel.getSaveSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Quan sát thông báo lỗi
        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }
}