package com.example.mysqldemo.controller;

import com.example.mysqldemo.model.User;
import com.example.mysqldemo.repository.UserRepository;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private static File pythonScript;

    @PostConstruct
    private void setUpPythonScript() throws IOException {
        // Read the bundled script as string
        String bundledScript = CharStreams.toString(
                new InputStreamReader(getClass().getResourceAsStream("/test.py"), Charsets.UTF_8));
        // Create a temp file with uuid appended to the name just to be safe
        pythonScript = File.createTempFile("script_" + UUID.randomUUID().toString(), ".py");
        // Write the string to temp file
        Files.write(bundledScript, pythonScript, Charsets.UTF_8);
    }

    @GetMapping("/user/{userId}")
    public User getUserById(@PathVariable Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @PostMapping("/user")
    public User saveUser(@RequestBody User user) {
        user.setDateCreated(new Date());
        return userRepository.save(user);
    }

    @GetMapping("/python")
    public String executePythonCode(@RequestParam String message) {
        System.out.println("Executing a python Script");
        try {
            // Helpful links
            // https://stackoverflow.com/questions/6603807/executing-a-shell-script-inside-a-jar-file-how-to-extract
            // https://stackoverflow.com/questions/25369490/executing-a-python-file-from-within-jar

            // ProcessBuilder runs the input arguments as a shell command
            ProcessBuilder processBuilder =
                    new ProcessBuilder(Arrays.asList("python",
                            pythonScript.getAbsolutePath(),
                            message));

            Process process = processBuilder.start();

            // Creates a buffered reader to process the output
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Sends the output from the BufferedReader to the screen, can be edited to go elsewhere
            StringBuilder stringBuffer = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                stringBuffer.append(s);
            }

            return stringBuffer.toString();
        } catch(Exception e){
            System.err.println("ERROR");
        };

        return "Error";
    }
}
