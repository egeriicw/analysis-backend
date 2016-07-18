package com.conveyal.taui.controllers;

import com.conveyal.taui.models.Project;
import com.conveyal.taui.persistence.Persistence;
import com.conveyal.taui.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.stream.Collectors;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.put;

/**
 * Created by matthewc on 7/12/16.
 */
public class ProjectController {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectController.class);

    public static Project getProject (Request req, Response res) {
        Project project = Persistence.projects.get(req.params("id"));

        if (project == null) halt(404);

        return project;
    }

    public static List<Project> getAllProjects (Request req, Response res) {
        String group = req.attribute("group");
        return Persistence.projects.values().stream()
                .filter(p -> group.equals(p.group))
                .collect(Collectors.toList());
    }

    public static Project createOrUpdate (Request req, Response res) {
        Project project = null;
        try {
            project = JsonUtil.objectMapper.readValue(req.body(), Project.class);
        } catch (Exception e) {
            LOG.error("Could not deserialize project from client", e);
            halt(400, "Bad project");
        }

        project.group = req.attribute("group");

        Project existing = Persistence.projects.get(project.id);

        if (existing != null && !existing.group.equals(project.group)) {
            halt(401, "Attempt to overwrite existing project belonging to other user");
        }

        Persistence.projects.put(project.id, project);

        return project;
    }

    public static void register () {
        get("/api/project", ProjectController::getAllProjects, JsonUtil.objectMapper::writeValueAsString);
        get("/api/project/:id", ProjectController::getProject, JsonUtil.objectMapper::writeValueAsString);
        get("/api/project/:project/scenarios", ScenarioController::getAllScenarios, JsonUtil.objectMapper::writeValueAsString);
        post("/api/project", ProjectController::createOrUpdate, JsonUtil.objectMapper::writeValueAsString);
        put("/api/project/:id", ProjectController::createOrUpdate, JsonUtil.objectMapper::writeValueAsString);
    }
}
