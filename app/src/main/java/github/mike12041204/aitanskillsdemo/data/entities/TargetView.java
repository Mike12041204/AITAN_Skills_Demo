package github.mike12041204.aitanskillsdemo.data.entities;

// Read only view of a target for the UI
public interface TargetView extends EntityView {

    // What the target is currently doing, placeholder until targets get commands of their own
    enum TargetCommand {
        NONE
    }

    TargetCommand getCommand();
}
