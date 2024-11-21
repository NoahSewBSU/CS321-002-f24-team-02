import java.io.*;
import java.util.regex.*;


public class SSHDataWrangler {


   public static void main(String[] args) {
       // Validate that exactly two arguments (input and output file paths) are provided
       if (args.length != 2) {
           System.out.println("Usage: java SSHDataWrangler <input_file> <output_file>");
           return;
       }


       // Input and output file paths from command-line arguments
       String inputFile = args[0];
       String outputFile = args[1];


       // Use try-with-resources to automatically close the file readers and writers
       try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {


           // Define regex patterns for different log formats
           // Main log pattern: Handles Accepted, Invalid, and Failed entries
           String logPattern = "(\\w{3})\\s+(\\d{1,2})\\s+(\\d{2}:\\d{2}:\\d{2})\\s+\\w+\\s+sshd\\[\\d+\\]:\\s+(Accepted password for|Invalid user|Failed password for invalid user)\\s+(\\S+)\\s+from\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+)(?:\\s+port\\s+\\d+\\s+ssh2)?";


           // Reverse mapping pattern: Handles reverse mapping log lines
           String reverseMappingPattern = "(\\w{3})\\s+(\\d{1,2})\\s+(\\d{2}:\\d{2}:\\d{2})\\s+\\w+\\s+sshd\\[\\d+\\]:\\s+reverse mapping checking getaddrinfo for .*\\[(\\d+\\.\\d+\\.\\d+\\.\\d+)\\].*";


           // Address mapping pattern: Handles Address log lines with hostname
           String addressPattern = "(\\w{3})\\s+(\\d{1,2})\\s+(\\d{2}:\\d{2}:\\d{2})\\s+\\w+\\s+sshd\\[\\d+\\]:\\s+Address\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+maps to\\s+(\\S+),.*";


           // Compile the patterns for efficient matching
           Pattern mainPattern = Pattern.compile(logPattern);
           Pattern reversePattern = Pattern.compile(reverseMappingPattern);
           Pattern addressPatternCompiled = Pattern.compile(addressPattern);


           String line; // Variable to hold each line read from the input file
           while ((line = reader.readLine()) != null) { // Read each line of the input file
               // Match the line against the main log pattern
               Matcher mainMatcher = mainPattern.matcher(line);
               // Match the line against the reverse mapping pattern
               Matcher reverseMatcher = reversePattern.matcher(line);
               // Match the line against the address mapping pattern
               Matcher addressMatcher = addressPatternCompiled.matcher(line);


               if (mainMatcher.matches()) {
                   // Extract details from a matching main log entry
                   String date = mainMatcher.group(1) + " " + mainMatcher.group(2); // Month and day
                   String time = mainMatcher.group(3); // Time
                   String type = mainMatcher.group(4).split(" ")[0]; // Log type (Accepted, Invalid, etc.)
                   String user = mainMatcher.group(5); // Username
                   String ip = mainMatcher.group(6); // IP address


                   // Write the formatted output to the output file
                   writer.write(String.format("%s %s %s %s [%s]\n", date, time, type, user, ip));
               } else if (reverseMatcher.matches()) {
                   // Extract details from a matching reverse mapping log entry
                   String date = reverseMatcher.group(1) + " " + reverseMatcher.group(2); // Month and day
                   String time = reverseMatcher.group(3); // Time
                   String ip = reverseMatcher.group(4); // IP address


                   // Write the formatted output to the output file
                   writer.write(String.format("%s %s ReverseMapping [%s]\n", date, time, ip));
               } else if (addressMatcher.matches()) {
                   // Extract details from a matching address mapping log entry
                   String date = addressMatcher.group(1) + " " + addressMatcher.group(2); // Month and day
                   String time = addressMatcher.group(3); // Time
                   String ip = addressMatcher.group(4); // IP address
                   String hostname = addressMatcher.group(5); // Hostname


                   // Write the formatted output to the output file
                   writer.write(String.format("%s %s Address %s [%s]\n", date, time, hostname, ip));
               } else {
                   // Log lines that do not match any known patterns
                   System.err.println("Skipped malformed line: " + line);
               }
           }


           // Inform the user that processing is complete
           System.out.println("Data wrangling complete. Output written to: " + outputFile);


       } catch (IOException e) {
           // Handle exceptions related to file I/O
           System.err.println("Error processing file: " + e.getMessage());
       }
   }
}




