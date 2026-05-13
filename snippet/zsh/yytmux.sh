tmux has-session -t yydev 2> /dev/null
if [ $? != 0 ]; then
    tmux new-session -s yydev -n kb -d
    tmux new-window -t yydev -n job
    tmux new-window -t yydev -n test
    tmux send-keys -t yydev:1 'cd ~/Desktop/learn/pknotes;clear' C-m
    tmux send-keys -t yydev:2 'cd ~/Downloads;clear' C-m
    tmux send-keys -t yydev:3 'cd ~/Downloads;clear' C-m
    tmux select-window -t yydev:1
fi

exit 0

