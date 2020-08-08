package repository.entity;

import java.nio.file.Path;
import java.util.Objects;

public class File {
    private int id ;
    private Path path ;
    private Path parent ;
    private String filename ;
    private User owner ;

    public File() {
    }

    public File(int id, Path path, Path parent, String filename, User owner) {
        this.id = id;
        this.path = path;
        this.parent = parent;
        this.filename = filename;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getParent() {
        return parent;
    }

    public void setParent(Path parent) {
        this.parent = parent;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return id == file.id &&
                path.equals(file.path) &&
                parent.equals(file.parent) &&
                filename.equals(file.filename) &&
                Objects.equals(owner, file.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path, parent, filename, owner);
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", path=" + path +
                ", parent=" + parent +
                ", filename='" + filename + '\'' +
                ", owner=" + owner +
                '}';
    }
}
