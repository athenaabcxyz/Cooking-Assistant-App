package com.example.cookingrecipesmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.cookingrecipesmanager.database.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String fragmentType = "Setting";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout layoutChangePassword, layoutLogout, layoutUser;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ImageView avatar;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User currentUser;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        layoutLogout = view.findViewById(R.id.layoutLogout);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        layoutUser = view.findViewById(R.id.layoutUser);
        avatar = view.findViewById(R.id.imageUser);
        getUser();

        layoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
            }
        });
        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_logout, null);

                TextView OK = view.findViewById(R.id.OK);
                TextView CANCEL = view.findViewById(R.id.CANCEL);


                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(view);

                AlertDialog dialog = builder.create();
                dialog.show();

                OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        firebaseAuth.signOut();
                        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                });

                CANCEL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        layoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), ChangeUserActivity.class));
            }
        });
        return view;
    }

    private void getUser() {
        db.collection("Users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentUser = documentSnapshot.toObject(User.class);
                        assert currentUser != null;
                        try {

                            if (currentUser.image != null) {
                                Picasso.get().load(currentUser.image).into(avatar);
                            }
                            else{
                                avatar.setImageResource(R.drawable.ic_avatar_default);
                            }

                        } catch (Exception e) {

                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        getUser();
    }
}