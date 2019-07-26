#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

void print_help_exit();

int main(int argc, char **argv) {
    if ( argc < 4 )
        print_help_exit();
    
    int failure = 0;

    failure |= setgid(atoi(argv[2]));
    failure |= setuid(atoi(argv[1]));
    
    if ( failure ) {
        perror("setuid|setgid");
        return 1;
    }
    
    execv(argv[3] ,argv + 3);

    perror("execv");

    return -1;
}

void print_help_exit() {
    printf("Usage: setuidgid <UID> <GID> <PROG> [ARGS...]\n");
    exit(1);
}
