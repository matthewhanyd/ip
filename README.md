# MattChatBot

MattChatBot is a greenfield Java chatbot project. Given below are instructions on how to set it up.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/MattChatBot.java` file, right-click it, and choose `Run MattChatBot.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
   M   M   AAA   TTTTT  TTTTT
   MM MM  A   A    T      T
   M M M  AAAAA    T      T
   M   M  A   A    T      T
   M   M  A   A    T      T

    CCC   H   H   AAA   TTTTT  BBBB    OOO   TTTTT
   C   C  H   H  A   A    T    B   B  O   O    T
   C      HHHHH  AAAAA    T    BBBB   O   O    T
   C   C  H   H  A   A    T    B   B  O   O    T
    CCC   H   H  A   A    T    BBBB    OOO     T

   Hello! I'm MattChatBot.
   What can I do for you?
   ____________________________________________________________
   ```
   The bot then waits for your commands. It tracks three kinds of task --
   `todo`, `deadline` and `event` -- and `mark N` / `unmark N` change a task's
   status. `list` shows everything, and `bye` exits:
   ```
   todo read book
   ____________________________________________________________
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 task in the list.
   ____________________________________________________________
   deadline return book /by Sunday
   ____________________________________________________________
   Got it. I've added this task:
     [D][ ] return book (by: Sunday)
   Now you have 2 tasks in the list.
   ____________________________________________________________
   event project meeting /from Mon 2pm /to 4pm
   ____________________________________________________________
   Got it. I've added this task:
     [E][ ] project meeting (from: Mon 2pm to: 4pm)
   Now you have 3 tasks in the list.
   ____________________________________________________________
   mark 1
   ____________________________________________________________
   Nice! I've marked this task as done:
     [T][X] read book
   ____________________________________________________________
   list
   ____________________________________________________________
   Here are the tasks in your list:
   1.[T][X] read book
   2.[D][ ] return book (by: Sunday)
   3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
   ____________________________________________________________
   bye
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

   If a command is not understood, or is missing a part it needs, the bot says
   what was wrong and how to write it instead:
   ```
   deadline /by Sunday
   ____________________________________________________________
   A deadline needs a description before the /by. Try: deadline return book /by Sunday
   ____________________________________________________________
   ```

## Running from the command line

```
javac -d bin src/main/java/*.java
java -cp bin MattChatBot
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
