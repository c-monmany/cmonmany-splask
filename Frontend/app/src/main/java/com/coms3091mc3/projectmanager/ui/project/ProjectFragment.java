package com.coms3091mc3.projectmanager.ui.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.coms3091mc3.projectmanager.R;
import com.coms3091mc3.projectmanager.app.AppController;
import com.coms3091mc3.projectmanager.data.Project;
import com.coms3091mc3.projectmanager.data.Task;
import com.coms3091mc3.projectmanager.data.Team;
import com.coms3091mc3.projectmanager.databinding.FragmentProjectBinding;
import com.coms3091mc3.projectmanager.store.ProjectDataModel;
import com.coms3091mc3.projectmanager.ui.dashboard.DashboardFragmentDirections;
import com.coms3091mc3.projectmanager.utils.Const;
import com.coms3091mc3.projectmanager.view.AddTeamDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectFragment extends Fragment {
    private FragmentProjectBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProjectBinding.inflate(inflater, container, false);
        binding.setModal(new ProjectDataModel(getContext()));
        View view = binding.getRoot();
        int id = (Integer) getArguments().get("projectID");
        String url = Const.API_SERVER + "/project/" + id;
        String tasksUrl = url + "/tasks";
        Button button = view.findViewById(R.id.add_project);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });

        GridView gridView = view.findViewById(R.id.projectTasks);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Task task = binding.getModal().tasksAdapter.getItem(i);
                ProjectFragmentDirections.ActionNavigationProjectToNavigationTask action = ProjectFragmentDirections.actionNavigationProjectToNavigationTask(task.getTaskID());
                Navigation.findNavController(view).navigate(action);
            }
        });

        JsonArrayRequest tasksRequest = new JsonArrayRequest(Request.Method.GET, tasksUrl, null,
                tasks -> {
                    try {
                        for (int i = 0; i < tasks.length(); i++) {
                            JSONObject object = (JSONObject) tasks.get(i);
                            Task task = new Task(
                                    object.getInt("taskID"),
                                    object.getString("taskName")
                            );
                            binding.getModal().tasksAdapter.add(task);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Logger.getLogger("json").log(Level.INFO, error.toString())
        );

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                project -> {
                    try {
                        binding.getModal().project.set(
                                new Project(
                                        project.getInt("projectID"),
                                        project.getString("projectName"),
                                        project.getString("dateCreated")
                                )
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Logger.getLogger("json").log(Level.INFO, error.toString()));
        AppController.getInstance().addToRequestQueue(request);
        AppController.getInstance().addToRequestQueue(tasksRequest);
        return view;
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.addTeam) {
                    addTeam();
                } else if (id == R.id.listMembers) {
                    listMembers();
                } else if (id == R.id.listTeams) {
                    listTeams();
                }
                return true;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_project_menu, popup.getMenu());
        popup.show();
    }

    public void listTeams() {
        String url = Const.API_SERVER + "/project/" + binding.getModal().project.get().getId() + "/" + "teams";
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        JsonArrayRequest teamsRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                teams -> {
                    alertBuilder.setTitle("List of Teams")
                            .setPositiveButton("BACK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    return;
                                }
                            });

                    String[] teamsList = new String[teams.length()];
                    for (int i = 0; i < teamsList.length; i++) {
                        try {
                            teamsList[i] = teams.getJSONObject(i).getString("teamName");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    alertBuilder.setItems(teamsList, null);
                    alertBuilder.create().show();
                },
                error -> {
                    VolleyLog.d("project_debug", "Error: " + error.toString());
                    error.printStackTrace();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );
        AppController.getInstance().addToRequestQueue(teamsRequest);
    }

    public void listMembers() {
        String url = Const.API_SERVER + "/project/" + binding.getModal().project.get().getId() + "/" + "users";
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        JsonArrayRequest usersRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                users -> {
                    alertBuilder.setTitle("List of Members")
                            .setPositiveButton("BACK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    return;
                                }
                            });

                    String[] memberList = new String[users.length()];
                    for (int i = 0; i < memberList.length; i++) {
                        try {
                            memberList[i] = users.getJSONObject(i).getString("username");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    alertBuilder.setItems(memberList, null);
                    alertBuilder.create().show();
                },
                error -> {
                    VolleyLog.d("project_debug", "Error: " + error.toString());
                    error.printStackTrace();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );
        AppController.getInstance().addToRequestQueue(usersRequest);
    }

    public void addTask() {

    }

    public void addTeam() {
        FragmentManager fragmentManager = getChildFragmentManager();
        AddTeamDialogFragment fragment = new AddTeamDialogFragment(new AddTeamDialogFragment.AddTeamDialogListener() {
            @Override
            public void onDialogPositiveClick(Team team) {
                Logger.getGlobal().log(Level.INFO, team.getTeamName());
                String url = Const.API_SERVER + "/project" + binding.getModal().project.get().getId() + "/addTeam";
                Map<String, String> params = new HashMap<String, String>();
                params.put("teamName", team.getTeamName());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), response -> {
                    Toast.makeText(getContext(), "Add Team Success!", Toast.LENGTH_SHORT).show();
                }, error -> {

                });
                AppController.getInstance().addToRequestQueue(request);
            }
        });
        fragment.show(fragmentManager, "addTeam");
    }

    public void addUser() {

    }

}