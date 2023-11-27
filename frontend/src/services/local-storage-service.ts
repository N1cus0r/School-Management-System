class LocalStorageService {
    static getLocalStorageToken() {
        const localStorageTokenJson = localStorage.getItem("access_token")
        return localStorageTokenJson ? JSON.parse(localStorageTokenJson) : null;
    }

    static setLocalStorageToken(token: string) {
        localStorage.setItem("access_token", JSON.stringify(token));
    }

    static delLocalStorageToken() {
        localStorage.removeItem("access_token");
    }
}

export default LocalStorageService