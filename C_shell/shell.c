#include <stdio.h>
#include <stdlib.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
//Lib includes

#define BUFFSIZE 4096
//Defines const bufsize



/** A limited shell program
 *  Allows users to implement function calls on a shell program
 *  No returns
 *  Type exit in command line to exit shell
 *  my first attempt
 */
int main()
{
    setbuf(stdout, NULL); // makes printf() unbuffered
    int n;
    char cmd[BUFFSIZE];
    char cwd[BUFFSIZE];
    getcwd(cwd, sizeof(cwd));    // working directory

    char *home = getenv("HOME"); // home  
    chdir(home);



        // loop that is infitne to prompt user
        while (1) {
                printf("Shell:");
                
        getcwd(cwd, sizeof(cwd));    //  working directory




               printf("~");
          char *match = cwd;
    int len = strlen(home);
    while ((match = strstr(match, home))) {
        *match = '\0';
        strcat(cwd, match+len);
                match++;
    }

  

       printf("%s", cwd);
                printf("$ ");
                n = read(STDIN_FILENO, cmd, BUFFSIZE);

                // if user enters a non-empty command
                if (n > 1) {
                        cmd[n-1] = '\0'; // replaces the final '\n' character with '\0' to make a proper string


        

//            printf("%s\n", getcwd(cwd, BUFFSIZE));

            int counter = 0;
            char * token = strtok(cmd, " ");
            int cont = strcmp("exit", token);
            if (!cont) exit(EXIT_FAILURE);
            int change = strcmp("cd", token);
            if(!change) {
                token = strtok(NULL, " ");

                if (token == NULL || !(strcmp("~", token))) {
                    token = home;
                }
                if (chdir(token) != 0) {
                    perror("chdir() failed");
                }
                continue;

            }
            // printf("%s\n", getcwd(cwd, BUFFSIZE));
            char * command [BUFFSIZE];
            //          printf("%s\n", token);
            // strcpy(command[counter], token);
            command[counter] = token;
            char* progname = command[0];
//            printf("%s\n", command[counter]);
            token = strtok(NULL, " ");



            pid_t pid;
            while (token != NULL)
            {

                int cont = strcmp("exit", token);
                if (!cont) exit(0);
                counter++;
                //              printf("%s\n", token);
                command[counter] = token;
//                strcpy(command[counter], token);

                token = strtok (NULL, " ");
//                counter

            }

            if ((pid = fork()) < 0) perror("fork");
            else if (pid == 0) { // child 



                int i = 0;
                while(command[i] != NULL) {
                    if (strcmp(command[i], "<") == 0) {
                        int fdi = open(command[i+1], O_RDONLY);
                        dup2(fdi, STDIN_FILENO);
                        command[i] = NULL;
                    } else if (strcmp(command[i], ">") == 0) {
                        int fdo = open(command[i+1], O_WRONLY | O_CREAT | O_TRUNC, 0644);
                        dup2(fdo, STDOUT_FILENO);
                        command[i] = NULL;
                    } else if (strcmp(command[i], ">>") == 0) {
                        int app = open(command[i+1], O_WRONLY | O_CREAT | O_APPEND, 0644);
                        dup2(app, STDOUT_FILENO);
                        command[i] = NULL;
                    }
                    i++;
                }

                execvp(progname, command);


                // if
            
               
            } else { // parent cakk
                int status;
                wait(&status);
               
            } // if

        }
        // if

    }

}// end of main
