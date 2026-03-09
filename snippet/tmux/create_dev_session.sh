if ! tmux has-session -t dev
then
  tmux new-session -s dev -n editor -d
  tmux send-keys -t dev 'cd ~/devproject' C-m
  tmux send-keys -t dev 'vim' C-m
  tmux split-window -v -t dev
  tmux select-layout -t dev main-horizontal
  tmux send-keys -t dev:1.2 'cd ~/devproject' C-m
  tmux new-window -n console -t dev
  tmux send-keys -t dev:2 'cd ~/devproject' C-m
  tmux select-window -t dev:1
fi

tmux attach -t dev
exit 0

