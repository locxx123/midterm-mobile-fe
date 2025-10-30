package com.example.midtermexercise;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.midtermexercise.api.ApiService;
import com.example.midtermexercise.api.RetrofitClient;
import com.example.midtermexercise.models.ContactResponse;
import com.example.midtermexercise.models.GroupAddResponse;
import com.example.midtermexercise.models.GroupResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactDetailFragment extends Fragment {

    private TextView tvFullName, tvPhone, tvNote, tvToggleFavorite, tvAddGroupLabel;
    private ImageView imgAvatar;
    private View btnAddToGroup;

    private String contactId;
    private boolean isFavorite;
    private boolean inAnyGroup = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_detail, container, false);

        tvFullName = view.findViewById(R.id.tvFullName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvNote = view.findViewById(R.id.tvNote);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvToggleFavorite = view.findViewById(R.id.tvToggleFavorite);
        btnAddToGroup = view.findViewById(R.id.btnAddToGroup);
        tvAddGroupLabel = view.findViewById(R.id.tvAddGroupLabel);

        // Gán click Nhắn tin / Gọi / Gửi tin nhắn (hàng dưới)
        View btnMessage = view.findViewById(R.id.btnMessage);
        if (btnMessage != null) btnMessage.setOnClickListener(v -> openSms());
        View btnCall = view.findViewById(R.id.btnCall);
        if (btnCall != null) btnCall.setOnClickListener(v -> openDialer());
        View tvSendMessage = view.findViewById(R.id.tvSendMessage);
        if (tvSendMessage != null) tvSendMessage.setOnClickListener(v -> openSms());

        if (getArguments() != null) {
            tvFullName.setText(getArguments().getString("name"));
            tvPhone.setText(getArguments().getString("phone"));
            contactId = getArguments().getString("id");
            isFavorite = getArguments().getBoolean("favorite", false);
        }

        updateFavoriteActionText();

        tvToggleFavorite.setOnClickListener(v -> toggleFavorite());

        if (btnAddToGroup != null) {
            // Mặc định mở dialog thêm nhóm
            btnAddToGroup.setOnClickListener(v -> showAddToGroupDialog());
            // Kiểm tra để đổi nhãn/hành vi nếu đã ở nhóm
            checkContactInAnyGroup();
        }

        return view;
    }

    private void openSms() {
        if (getContext() == null) return;
        String phone = tvPhone != null ? tvPhone.getText().toString().trim() : "";
        if (phone.isEmpty()) {
            Toast.makeText(getContext(), "Không có số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + phone));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Không mở được ứng dụng nhắn tin", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDialer() {
        if (getContext() == null) return;
        String phone = tvPhone != null ? tvPhone.getText().toString().trim() : "";
        if (phone.isEmpty()) {
            Toast.makeText(getContext(), "Không có số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Không mở được ứng dụng gọi điện", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFavoriteActionText() {
        if (tvToggleFavorite != null) {
            tvToggleFavorite.setText(isFavorite ? "Xoá khỏi Mục ưa thích" : "Thêm vào Mục ưa thích");
        }
    }

    private void toggleFavorite() {
        if (contactId == null || getContext() == null) {
            Toast.makeText(getContext(), "Thiếu thông tin liên hệ", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService api = RetrofitClient.getApiService(getContext());
        Call<ContactResponse> call = isFavorite
                ? api.removeFavorite(contactId)
                : api.addFavorite(contactId);

        call.enqueue(new Callback<ContactResponse>() {
            @Override
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getContact() != null) {
                    isFavorite = response.body().getContact().isFavorite();
                    updateFavoriteActionText();
                    Toast.makeText(getContext(), isFavorite ? "Đã thêm vào yêu thích" : "Đã xoá khỏi yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyAddGroupUiState() {
        if (btnAddToGroup == null || tvAddGroupLabel == null) return;
        if (inAnyGroup) {
            tvAddGroupLabel.setText("đã có nhóm");
            btnAddToGroup.setOnClickListener(v -> navigateToGroups());
        } else {
            tvAddGroupLabel.setText("thêm nhóm");
            btnAddToGroup.setOnClickListener(v -> showAddToGroupDialog());
        }
    }

    private void navigateToGroups() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new GroupFragment())
                .addToBackStack(null)
                .commit();
    }

    private void checkContactInAnyGroup() {
        if (getContext() == null || contactId == null) return;
        ApiService api = RetrofitClient.getApiService(requireContext());
        api.getGroups().enqueue(new Callback<GroupResponse>() {
            @Override
            public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().groups != null) {
                    boolean found = false;
                    for (GroupResponse.GroupItem g : response.body().groups) {
                        if (g.contacts != null) {
                            for (GroupResponse.ContactItem c : g.contacts) {
                                if (contactId.equals(c._id)) { found = true; break; }
                            }
                        }
                        if (found) break;
                    }
                    inAnyGroup = found;
                    applyAddGroupUiState();
                }
            }
            @Override public void onFailure(Call<GroupResponse> call, Throwable t) { /* ignore */ }
        });
    }

    private void showAddToGroupDialog() {
        if (getContext() == null || contactId == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_select_group, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        ListView lvGroups = dialogView.findViewById(R.id.lvGroups);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        LinearLayout btnAdd = dialogView.findViewById(R.id.btnAdd);
        ProgressBar spinner = dialogView.findViewById(R.id.progressSpinnerAdd);
        TextView tvAddText = dialogView.findViewById(R.id.tvAddText);

        final List<GroupResponse.GroupItem> groups = new ArrayList<>();
        final int[] selectedIndex = new int[]{-1};
        final int[] preSelectedIndex = new int[]{-1};

        ApiService api = RetrofitClient.getApiService(requireContext());
        api.getGroups().enqueue(new Callback<GroupResponse>() {
            @Override
            public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().groups != null) {
                    groups.clear();
                    groups.addAll(response.body().groups);

                    List<String> names = new ArrayList<>();
                    for (GroupResponse.GroupItem g : groups) names.add(g.name);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_list_item_single_choice, names);
                    lvGroups.setAdapter(adapter);
                    lvGroups.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                    int foundIndex = -1;
                    for (int i = 0; i < groups.size(); i++) {
                        GroupResponse.GroupItem g = groups.get(i);
                        if (g.contacts != null) {
                            for (GroupResponse.ContactItem c : g.contacts) {
                                if (contactId.equals(c._id)) { foundIndex = i; break; }
                            }
                        }
                        if (foundIndex != -1) break;
                    }
                    preSelectedIndex[0] = foundIndex;
                    selectedIndex[0] = foundIndex;

                    if (foundIndex != -1) {
                        lvGroups.setItemChecked(foundIndex, true);
                        tvAddText.setText("Đã thêm");
                        btnAdd.setEnabled(false);
                        btnAdd.setAlpha(0.6f);
                    } else {
                        tvAddText.setText("Thêm");
                        btnAdd.setEnabled(true);
                        btnAdd.setAlpha(1f);
                    }

                    lvGroups.setOnItemClickListener((parent, v, position, id) -> {
                        selectedIndex[0] = position;
                        if (preSelectedIndex[0] == position && preSelectedIndex[0] != -1) {
                            tvAddText.setText("Đã thêm");
                            btnAdd.setEnabled(false);
                            btnAdd.setAlpha(0.6f);
                        } else {
                            tvAddText.setText("Thêm");
                            btnAdd.setEnabled(true);
                            btnAdd.setAlpha(1f);
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Không thể tải nhóm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GroupResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            if (preSelectedIndex[0] != -1 && selectedIndex[0] == preSelectedIndex[0]) {
                dialog.dismiss();
                return;
            }
            if (selectedIndex[0] == -1) {
                Toast.makeText(requireContext(), "Vui lòng chọn nhóm", Toast.LENGTH_SHORT).show();
                return;
            }
            String groupId = groups.get(selectedIndex[0])._id;
            if (groupId == null) {
                Toast.makeText(requireContext(), "Nhóm không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            setAddLoading(btnAdd, spinner, true);

            Map<String, String> body = new HashMap<>();
            body.put("contactId", contactId);

            api.addContactToGroup(groupId, body).enqueue(new Callback<GroupAddResponse>() {
                @Override
                public void onResponse(Call<GroupAddResponse> call, Response<GroupAddResponse> response) {
                    setAddLoading(btnAdd, spinner, false);
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Đã thêm vào nhóm", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        inAnyGroup = true;
                        applyAddGroupUiState();
                    } else {
                        Toast.makeText(requireContext(), "Thêm vào nhóm thất bại (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GroupAddResponse> call, Throwable t) {
                    setAddLoading(btnAdd, spinner, false);
                    Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void setAddLoading(LinearLayout btnAdd, ProgressBar spinner, boolean isLoading) {
        btnAdd.setEnabled(!isLoading);
        btnAdd.setAlpha(isLoading ? 0.6f : 1f);
        spinner.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}