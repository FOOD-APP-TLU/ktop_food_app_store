package com.example.ktop_food_app_store.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.databinding.ActivityAddItemBinding;
import com.example.ktop_food_app_store.model.repository.AddItemRepository;
import com.example.ktop_food_app_store.viewmodel.AddItemViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {

    private ActivityAddItemBinding binding;
    private AddItemViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new AddItemViewModel(new AddItemRepository());
            }
        }).get(AddItemViewModel.class);

        setupSpinners();
        setupListeners();
        observeViewModel();
    }

    private void setupSpinners() {
        setupTimeSpinner();
        setupCategorySpinner();
    }

    private void setupTimeSpinner() {
        List<String> timeOptions = new ArrayList<>();
        timeOptions.add("Select Time");
        timeOptions.add("5 - 10 mins");
        timeOptions.add("10 - 15 mins");
        timeOptions.add("15 - 20 mins");

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeOptions);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.timeSpinner.setAdapter(timeAdapter);
    }

    private void setupCategorySpinner() {
        List<String> categoryOptions = new ArrayList<>();
        categoryOptions.add("Select Category");
        categoryOptions.add("Spaghetti");
        categoryOptions.add("Hotdog");
        categoryOptions.add("Chicken");
        categoryOptions.add("Hamburger");
        categoryOptions.add("Bingsu");
        categoryOptions.add("Pizza");
        categoryOptions.add("Drink");
        categoryOptions.add("Other");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryOptions);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);
    }

    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> finish());
        binding.addItemButton.setOnClickListener(v -> viewModel.addItem());

        binding.urlImageIcon.setOnClickListener(v -> handleImagePreview());

        binding.timeIcon.setOnClickListener(v -> binding.timeSpinner.performClick());
        binding.categoryIcon.setOnClickListener(v -> binding.categorySpinner.performClick());

        // Gắn TextWatcher và Spinner Listener từ ViewModel
        binding.itemNameEditText.addTextChangedListener(viewModel.getTextWatcher());
        binding.itemPriceEditText.addTextChangedListener(viewModel.getTextWatcher());
        binding.urlImageEditText.addTextChangedListener(viewModel.getTextWatcher());
        binding.descriptionEditText.addTextChangedListener(viewModel.getTextWatcher());

        binding.timeSpinner.setOnItemSelectedListener(viewModel.getSpinnerListener());
        binding.categorySpinner.setOnItemSelectedListener(viewModel.getSpinnerListener());
    }

    private void handleImagePreview() {
        String imageUrl = binding.urlImageEditText.getText().toString().trim();

        // Kiểm tra xem URL có rỗng không
        if (imageUrl.isEmpty()) {
            binding.urlImageEditText.setError("Please enter an image URL");
            binding.imagePreview.setImageResource(R.drawable.logo_single);
            viewModel.setUrlImage(""); // Không chấp nhận URL rỗng
            return;
        }

        // Kiểm tra định dạng URL
        if (!isValidUrl(imageUrl)) {
            binding.urlImageEditText.setError("Invalid URL format");
            binding.imagePreview.setImageResource(R.drawable.logo_single);
            viewModel.setUrlImage(""); // Không chấp nhận URL không hợp lệ
            Toast.makeText(this, "Please enter a valid image URL", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu URL hợp lệ, thử tải ảnh
        binding.imagePreview.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.logo_single)
                .error(R.drawable.logo_single) // Hiển thị placeholder nếu tải ảnh thất bại
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                    @Override
                    public boolean onLoadFailed(com.bumptech.glide.load.engine.GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                        // Nếu tải ảnh thất bại, hiển thị lỗi và không chấp nhận URL
                        binding.urlImageEditText.setError("Failed to load image from URL");
                        binding.imagePreview.setImageResource(R.drawable.logo_single);
                        viewModel.setUrlImage(""); // Không chấp nhận URL không tải được ảnh
                        Toast.makeText(AddItemActivity.this, "Failed to load image. Please check the URL.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        // Nếu tải ảnh thành công, chấp nhận URL
                        viewModel.setUrlImage(imageUrl);
                        return false;
                    }
                })
                .into(binding.imagePreview);
    }

    // Hàm kiểm tra định dạng URL
    private boolean isValidUrl(String url) {
        // Kiểm tra xem URL có bắt đầu bằng http:// hoặc https:// không
        if (url == null) {
            return false;
        }
        // Sử dụng Patterns.WEB_URL để kiểm tra định dạng URL
        return Patterns.WEB_URL.matcher(url).matches() && (url.startsWith("http://") || url.startsWith("https://"));
    }

    private void observeViewModel() {
        // Quan sát trạng thái form hợp lệ
        viewModel.getIsFormValid().observe(this, isValid -> {
            if (isValid) {
                binding.addItemButton.setBackgroundResource(R.drawable.custom_bg_success);
                binding.addItemButton.setEnabled(true);
            } else {
                binding.addItemButton.setBackgroundResource(R.drawable.custom_bg_default);
                binding.addItemButton.setEnabled(false);
            }
        });

        // Quan sát lỗi của các trường
        viewModel.getItemNameError().observe(this, error -> binding.itemNameEditText.setError(error));
        viewModel.getItemPriceError().observe(this, error -> binding.itemPriceEditText.setError(error));
        viewModel.getUrlImageError().observe(this, error -> binding.urlImageEditText.setError(error));
        viewModel.getTimeError().observe(this, error -> binding.timeErrorTextView.setText(error));
        viewModel.getCategoryError().observe(this, error -> binding.categoryErrorTextView.setText(error));

        // Quan sát kết quả thêm món ăn
        viewModel.getAddItemResult().observe(this, result -> {
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            if (result.equals("Item added successfully")) {
                clearInputFields();
            }
        });

        // Cập nhật dữ liệu từ UI vào ViewModel
        binding.itemNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setItemName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.itemPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setItemPrice(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.urlImageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Không tự động cập nhật URL vào ViewModel ở đây
                // Chỉ cập nhật khi URL được xác nhận hợp lệ trong handleImagePreview()
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.timeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                viewModel.setTime(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                viewModel.setTime("Select Time");
            }
        });

        binding.categorySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                viewModel.setCategory(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                viewModel.setCategory("Select Category");
            }
        });
    }

    private void clearInputFields() {
        binding.itemNameEditText.setText("");
        binding.itemPriceEditText.setText("");
        binding.urlImageEditText.setText("");
        binding.descriptionEditText.setText("");
        binding.timeSpinner.setSelection(0);
        binding.categorySpinner.setSelection(0);
        binding.imagePreview.setImageResource(R.drawable.logo_single);
        binding.imagePreview.setVisibility(View.VISIBLE);
    }
}