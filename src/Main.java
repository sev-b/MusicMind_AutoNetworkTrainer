import java.io.File;

public class Main {
    private static final String SPLITTED_MIDIS = "/mnt/network_training/midifiles/splitted_midis/";
    private static final String TFRECORD_FILE = "/mnt/network_training/melody_rnn/tfrecords/%s.tfrecord";
    private static final String RUN_DIRECTORY = "/mnt/network_training/melody_rnn/logdir/run_%s";
    private static final String OUTPUT_DIRECTORY = "/mnt/network_training/melody_rnn/genres/%s";
    private static final String WORKING_DIRECTORY = "/root/magenta";
    private static int NUM_TRAINING_STEPS = 20000;

    /**
     *  Executes specific bash script files, which run different tensorflow commands to generate note sequences and datasets
     *  and then train the neural network itself
     * @param args: args[0] can be used to change the number of training cycles (default = 20000)
     */
    public static void main(String[] args) {

        if (args.length > 0) {
            NUM_TRAINING_STEPS = Integer.parseInt(args[0]);
        }

        File splittedMidis = new File(SPLITTED_MIDIS);
        File[] midiDirs = splittedMidis.listFiles();
        for (File midiDir : midiDirs) {
            System.out.println(midiDir.getName());
        }

        if (splittedMidis.listFiles() != null) {
            for (File midiDir : midiDirs) {
                String currName = midiDir.getName();
                try {
                    System.out.println("Name of network: " + currName);
                    //if (Files.notExists(Paths.get(String.format(TFRECORD_FILE, currName)))) { //Script is only executed if the target doesn't already exist
                    System.out.println("generate_note_sequence for " + currName);
                    Process generate_note_sequence = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", "sudo bash generate_note_sequence.bash " + currName}, null, new File(WORKING_DIRECTORY));
                    generate_note_sequence.waitFor();
                    System.out.println();
                    //}

                    //if (Files.notExists(Paths.get(String.format(OUTPUT_DIRECTORY, currName)))) { //Script is only executed if the target doesn't already exist
                    System.out.println("generate_dataset for " + currName);
                    Process generate_dataset = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", "sudo bash generate_dataset.bash " + currName}, null, new File(WORKING_DIRECTORY));
                    generate_dataset.waitFor();
                    System.out.println();
                    //}

                    //if (Files.notExists(Paths.get(String.format(RUN_DIRECTORY, currName)))) { //Script is only executed if the target doesn't already exist
                    System.out.println("train_network for " + currName);
                    Process train_network = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", "sudo bash train_network.bash " + currName + " " + NUM_TRAINING_STEPS}, null, new File(WORKING_DIRECTORY));
                    train_network.waitFor();
                    System.out.println("Network trained");
                    //}

//                    File log = new File("networkTrainingLog.txt");
//                    log.delete();
                } catch (Exception e) {
                    System.err.println("Could not start magenta! No training or generating will be done. Reason: " + e.getLocalizedMessage());
                    e.printStackTrace();
                    System.err.println();
                }
            }
        }
    }
}