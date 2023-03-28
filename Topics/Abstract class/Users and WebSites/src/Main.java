
abstract class BaseEntity {
    protected long id;

    protected long version;

    public BaseEntity(long id, long version) {
        this.id = id;
        this.version = version;
    }


    public abstract long getId();

    public abstract void setId(long id);

    public abstract long getVersion();

    public abstract void setVersion(long version);
}


class User extends BaseEntity{

    protected String name;

    public User() {
        super(0,0);
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Visit extends BaseEntity {

    protected User user;

    protected WebSite site;

    public Visit() {
        super(0,0);
    }


    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public WebSite getSite() {
        return site;
    }

    public void setSite(WebSite site) {
        this.site = site;
    }
}

class WebSite extends BaseEntity {

    protected String url;

    public WebSite() {
        super(0,0);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}