package com.coms3091mc3.projectmanager.ui.team;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.coms3091mc3.projectmanager.R;
import com.coms3091mc3.projectmanager.TeamActivity;
import com.coms3091mc3.projectmanager.app.AppController;
import com.coms3091mc3.projectmanager.data.Project;
import com.coms3091mc3.projectmanager.data.Team;
import com.coms3091mc3.projectmanager.data.User;
import com.coms3091mc3.projectmanager.databinding.FragmentTeamBinding;
import com.coms3091mc3.projectmanager.store.TeamDataModel;
import com.coms3091mc3.projectmanager.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeamFragment extends Fragment {
    FragmentTeamBinding binding;

    ListView lv;
    private JSONArray usersArray = new JSONArray();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeamBinding.inflate(inflater, container, false);
        binding.setModal(new TeamDataModel());
        int id = (Integer) getArguments().get("teamID");
        getTeamRequest(id);
        View view = binding.getRoot();
        Button button = view.findViewById(R.id.btnAddUser);
        lv = (ListView) view.findViewById(R.id.user_list);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMember();
            }
        });
        return view;
    }

    void getTeamRequest(int id) {
        String url = Const.API_SERVER + "/team/" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                object -> {
                    try {
                        String teamName = object.getString("teamName");
                        int teamID = object.getInt("teamID");
                        Team team = new Team(teamID, teamName);
                        binding.getModal().team.set(team);

                        getMembersRequest(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                }
        );
        AppController.getInstance().addToRequestQueue(request);
    }

    void addMember() {
        Map<String, String> params = new HashMap<String, String>();
        Context context = getContext();

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        final EditText dialogInput = new EditText(context);
        dialogInput.setLayoutParams(lp);
        alertBuilder.setView(dialogInput);

        alertBuilder.setMessage("Enter username")
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialogInput.getText().toString().length() < 4) { //at least 4 characters
                            Toast.makeText(context.getApplicationContext(), "Name must be at least 4 characters", Toast.LENGTH_LONG).show();
                            return;
                        }
                        params.put("username", dialogInput.getText().toString());
                        // todo: query to add team member
                        addMemberRequest(params);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        return;
                    }
                });
        // Create the AlertDialog object and return it
        alertBuilder.create().show();
    }

    void getMembersRequest(int id){ //get list of users
        String url = Const.API_SERVER + "/team/" + id + "/users";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                users -> {
                    try {
                        List<String> userNameList = new ArrayList<String>();
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject object = (JSONObject) users.get(i);
                            User user = new User(
                                    object.getInt("user_id"),
                                    object.getString("username"),
                                    object.getString("fullname")
                            );
                            usersArray.put(object);
                            userNameList.add(object.getString("fullname"));
//                            binding.getModal().team.
                            Log.d("project_debug",object.toString());
                        }
                        ArrayAdapter<String> userName = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_list_item_1,
                                userNameList);
                        lv.setAdapter(userName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Logger.getLogger("json").log(Level.INFO, error.toString())
        );

        AppController.getInstance().addToRequestQueue(request);
    }

    void addMemberRequest(Map<String, String> params) { //add member to team
        String url = Const.API_SERVER + "/team/" + binding.getModal().team.get().getTeamID() +"/addUser";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(params),
                response -> {
                    try {
                        Log.d("project_debug",response.getString("message"));
                        Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                }
        );
        AppController.getInstance().addToRequestQueue(request);
    }
}
