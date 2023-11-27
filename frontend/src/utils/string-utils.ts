class StringUtils {
    static getTitleString(str: string) {
        return (
            str.charAt(0).toUpperCase() +
            str.slice(1).toLowerCase()
        );
    }
}

export default StringUtils
